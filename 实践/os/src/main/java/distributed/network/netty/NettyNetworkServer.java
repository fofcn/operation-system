package distributed.network.netty;

import distributed.network.NetworkServer;
import distributed.network.SendCallback;
import distributed.network.common.Pair;
import distributed.network.common.TlsMode;
import distributed.network.config.NettyServerConfig;
import distributed.network.exception.NetworkConnectException;
import distributed.network.exception.NetworkSendRequestException;
import distributed.network.exception.NetworkTimeoutException;
import distributed.network.interceptor.RequestInterceptor;
import distributed.network.processor.NettyRequestProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author errorfatal89@gmail.com
 */
public class NettyNetworkServer extends NettyNetworkAbstract implements NetworkServer {
    private static final Logger log = LoggerFactory.getLogger("network-server");
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupBoss;
    private final EventLoopGroup eventLoopGroupSelector;

    private final DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final ExecutorService publicExecutor;
    private final NettyServerConfig nettyServerConfig;
    private final ChannelEventListener channelEventListener;

    private static final byte HANDSHAKE_MAGIC_CODE = 0x16;
    private static final String HANDSHAKE_HANDLER_NAME = "handshakeHandler";
    private static final String TLS_HANDLER_NAME = "sslHandler";
    private static final String FILE_REGION_ENCODER_NAME = "fileRegionEncoder";

    public NettyNetworkServer(final NettyServerConfig nettyServerConfig) {
        this(nettyServerConfig, null);
    }

    public NettyNetworkServer(NettyServerConfig nettyServerConfig, ChannelEventListener channelEventListener) {
        this.serverBootstrap = new ServerBootstrap();
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyBoss_%d", threadIndex.getAndIncrement()));
            }
        });

        this.eventLoopGroupSelector = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            private int threadTotal = nettyServerConfig.getServerSelectorThreads();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
            }
        });

        this.nettyServerConfig = nettyServerConfig;

        int publicThreadNums = 4;
        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(//
                4,
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        this.channelEventListener = channelEventListener;

        buildSSLContext();
    }

    /**
     * 启动服务器
     */
    @Override
    public void start() {
        ServerBootstrap childHandler =
                this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector).channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                        .option(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                        .localAddress(new InetSocketAddress(nettyServerConfig.getListenPort()))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(
                                        defaultEventExecutorGroup,
                                        new NettyEncoder(),
                                        new NettyDecoder(),
                                        new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                                        new NettyConnectManagerHandler(NettyNetworkServer.this),
                                        new NettyServerHandler());

                                if (nettyServerConfig.isUseTLS()) {
                                    ch.pipeline()
                                            .addFirst(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME,
                                                    new HandShakeHandler());
                                }
                            }
                        });

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            log.info("binding local port {}", addr.getPort());
        } catch (InterruptedException e) {
            log.error("start server failed", e);
        }

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }

        scanResponseTable();
    }

    /**
     * 停止服务器
     */
    @Override
    public void shutdown() {
        super.shutdown();
        this.eventLoopGroupBoss.shutdownGracefully();

        this.eventLoopGroupSelector.shutdownGracefully();

        if (this.defaultEventExecutorGroup != null) {
            this.defaultEventExecutorGroup.shutdownGracefully();
        }

        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                log.error("NettyRemotingServer shutdown exception, ", e);
            }
        }

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.shutdown();
        }
    }

    /**
     * 注册消息拦截器（应用于所有消息）
     *
     * @param interceptor 消息拦截器
     */
    @Override
    public void registerInterceptor(RequestInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    @Override
    public NetworkCommand sendSync(Channel channel, NetworkCommand request, long timeoutMillis)
            throws InterruptedException, NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException {
        return sendSyncImpl(channel, request, timeoutMillis);
    }

    @Override
    public void sendOneway(Channel channel, NetworkCommand request, long timeoutMillis, SendCallback sendCallback)
            throws InterruptedException, NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException {
        sendOnewayImpl(channel, request, timeoutMillis);
    }

    @Override
    public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executorService) {
        if (executorService == null) {
            executorService = this.publicExecutor;
        }

        Pair<NettyRequestProcessor, ExecutorService> pair = new Pair<NettyRequestProcessor, ExecutorService>(processor, executorService);
        processorTable.put(requestCode, pair);
    }

    @Override
    protected ChannelEventListener getChannelEventListener() {
        return this.channelEventListener;
    }


    /**
     * 组装SSL上下文
     */
    private void buildSSLContext() {
        if (nettyServerConfig.isUseTLS()) {
            TlsSystemConfig.tlsConfigFile = nettyServerConfig.getTlsFile();
            TlsMode tlsMode = TlsSystemConfig.tlsMode;
            log.info("Server is running in TLS {} mode", tlsMode.getName());
        }
    }

    /**
     * 消息处理
     */
    class NettyServerHandler extends SimpleChannelInboundHandler<NetworkCommand> {

        /**
         * 处理消息
         *
         * @param ctx channel上下文
         * @param msg 消息
         * @throws Exception 消息处理异常
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, NetworkCommand msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }

    /**
     * TLS handler
     *
     * @author errorfatal89@gmail.com
     */
    class HandShakeHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            msg.markReaderIndex();

            byte b = msg.getByte(0);
            if (b == HANDSHAKE_MAGIC_CODE && sslContext != null) {
                ctx.pipeline()
                        .addAfter(defaultEventExecutorGroup,
                                HANDSHAKE_HANDLER_NAME, TLS_HANDLER_NAME, sslContext.newHandler(ctx.channel().alloc()))
                        .addAfter(defaultEventExecutorGroup,
                                TLS_HANDLER_NAME, FILE_REGION_ENCODER_NAME, new FileRegionEncoder());
                log.info("Handlers prepended to channel pipeline to establish SSL connection");
            }

            msg.resetReaderIndex();

            try {
                ctx.pipeline().remove(this);
            } catch (NoSuchElementException e) {
                log.error("Error while removing HandshakeHandler", e);
            }

            ctx.fireChannelRead(msg.retain());
        }
    }
}
