
package com.github.futurefs.netty.netty;

import com.github.futurefs.netty.NetworkClient;
import com.github.futurefs.netty.SendCallback;
import com.github.futurefs.netty.common.Pair;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.enums.NettyEventType;
import com.github.futurefs.netty.exception.NetworkConnectException;
import com.github.futurefs.netty.exception.NetworkSendRequestException;
import com.github.futurefs.netty.exception.NetworkTimeoutException;
import com.github.futurefs.netty.interceptor.RequestInterceptor;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.netty.util.NetworkUtil;
import com.github.futurefs.netty.util.StringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author errorfatal89@gmail.com
 */
public class NettyNetworkClient extends NettyNetworkAbstract implements NetworkClient {
    private static final Logger log = LoggerFactory.getLogger("Network");
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroupWorker;
    private final ConcurrentHashMap<String, ChannelWrapper> channelTables = new ConcurrentHashMap<>();
    private final ReentrantLock lockChannelTables = new ReentrantLock();
    private final NettyClientConfig nettyClientConfig;
    private final ThreadPoolExecutor publicExecutor;
    private final ChannelEventListener channelEventListener;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyNetworkClient(final NettyClientConfig nettyClientConfig) {
        this(nettyClientConfig, null);
    }

    public NettyNetworkClient(NettyClientConfig nettyClientConfig, ChannelEventListener channelEventListener) {
        this.nettyClientConfig = nettyClientConfig;
        this.eventLoopGroupWorker = new NioEventLoopGroup(nettyClientConfig.getClientWorkerThreads(), new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
            }
        });

        int publicThreadNums = 4;
        this.publicExecutor = new ThreadPoolExecutor(publicThreadNums, publicThreadNums, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(nettyClientConfig.getQueueCapacity()), new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
            }
        }, (r, executor) -> log.error(r.toString() + "reject from " + executor.toString()));

        this.channelEventListener = channelEventListener;
    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(//
                1, //
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)//
                //启用Nagle算法
                .option(ChannelOption.TCP_NODELAY, true)
                //
                .option(ChannelOption.SO_KEEPALIVE, false)
                //
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                //
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                //
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                //
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(//
                                defaultEventExecutorGroup, //
                                new NettyEncoder(), //
                                new NettyDecoder(), //
                                new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()), //
                                new NettyConnetManageHandler(), //
                                new NettyClientHandler());
                    }
                });

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }

        scanResponseTable();
    }

    /**
     * 停止客户端
     */
    @Override
    public void shutdown() {
        super.shutdown();
        this.eventLoopGroupWorker.shutdownGracefully();

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
     * 发送同步消息
     * @param addr host地址
     * @param request 请求命令
     * @param timeoutMillis 超时时间
     * @return
     * @throws InterruptedException
     * @throws NetworkTimeoutException
     * @throws NetworkSendRequestException
     * @throws NetworkConnectException
     */
    @Override
    public NetworkCommand sendSync(final String addr, final NetworkCommand request, long timeoutMillis)
            throws InterruptedException, NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException {
        Channel channel = getOrCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            return sendSyncImpl(channel, request, timeoutMillis);
        } else {
            this.closeChannel(addr, channel);
            throw new NetworkConnectException(addr);
        }
    }

    /**
     * 发送oneway消息
     * @param addr host地址
     * @param request 请求命令
     * @param timeoutMillis 超时时间
     * @param sendCallback 发送回调
     * @throws InterruptedException
     * @throws NetworkTimeoutException
     * @throws NetworkSendRequestException
     * @throws NetworkConnectException
     */
    @Override
    public void sendOneway(String addr, NetworkCommand request, long timeoutMillis, SendCallback sendCallback)
            throws InterruptedException,
            NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException {
        final Channel channel = this.getOrCreateChannel(addr);
        if (channel != null && channel.isActive()) {
            try {
                this.sendOnewayImpl(channel, request, timeoutMillis);
            } catch (NetworkSendRequestException e) {
                log.warn("invokeOneway: send request exception, so close the channel[{}]", addr);
                this.closeChannel(addr, channel);
                throw e;
            }
        } else {
            this.closeChannel(addr, channel);
            throw new NetworkConnectException(addr);
        }
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
     * 注册消息拦截器
     * @param interceptor 消息拦截器
     */
    @Override
    public void registerInterceptor(RequestInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * 关闭channel
     * @param channel channel
     */
    public void closeChannel(final Channel channel) {
        if (null == channel) {
            return;
        }

        try {
            if (this.lockChannelTables.tryLock(3000, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = null;
                    String addrRemote = null;
                    for (Map.Entry<String, ChannelWrapper> entry : channelTables.entrySet()) {
                        String key = entry.getKey();
                        ChannelWrapper prev = entry.getValue();
                        if (prev.getChannel() != null && prev.getChannel() == channel) {
                            prevCW = prev;
                            addrRemote = key;
                            break;
                        }
                    }

                    if (null == prevCW) {
                        log.info("eventCloseChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        log.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                        //关闭chanel
                        NetworkUtil.closeChannel(channel);
                    }
                } catch (Exception e) {
                    log.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                log.warn("closeChannel: try to lock channel table, but timeout, {}ms", 3000);
            }
        } catch (InterruptedException e) {
            log.error("closeChannel exception", e);
        }
    }

    /**
     * 关闭channel
     * @param addr server地址
     * @param channel channel
     */
    public void closeChannel(final String addr, Channel channel) {
        if (StringUtil.isEmpty(addr) || channel == null) {
            return;
        }

        boolean shouldRemove = true;
        ChannelWrapper prev = channelTables.get(addr);
        if (prev == null) {
            log.info("closeChannel: the channel[{}] has been removed from the channel table before", addr);
            shouldRemove = false;
        } else if (prev.getChannel() != channel) {
            log.info("closeChannel: the channel[{}] has been closed before, and has been created again, nothing to do.",
                    addr);
            shouldRemove = false;
        }

        if (shouldRemove) {
            channelTables.remove(addr);
        }

        //关闭chanel
        NetworkUtil.closeChannel(channel);
    }

    /**
     * 创建channel，如果存在则复用
     * @param addr server地址
     * @throws InterruptedException 锁被中断
     */
    private Channel getOrCreateChannel(final String addr) throws InterruptedException {
        if (StringUtil.isEmpty(addr)) {
            return null;
        }

        ChannelWrapper channelWrapper = channelTables.get(addr);
        if (channelWrapper != null && channelWrapper.isOK()) {
            return channelWrapper.getChannel();
        }

        try {
            return createChannel(addr);
        } catch (InterruptedException e) {
            log.error("", e);
            throw e;
        }
    }

    /**
     * 创建channel，如果存在则复用
     * @param addr server地址
     * @return channel
     * @throws InterruptedException
     */
    private Channel createChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            cw.getChannel().close();
            channelTables.remove(addr);
        }

        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));

        if (lockChannelTables.tryLock(3000, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = true;
                cw = this.channelTables.get(addr);
                if (cw != null) {
                    if (cw.isOK()) {
                        return cw.getChannel();
                    } else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    } else {
                        createNewConnection = true;
                        channelTables.remove(cw.getChannel());
                    }
                }

                if (createNewConnection) {
                    ChannelFuture channelFuture = this.bootstrap.connect(isa);
                    log.info("createChannel: begin to connect remote host[{}] asynchronously ", addr);
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(addr, cw);
                }

            }  finally {
                lockChannelTables.unlock();
            }
        }

        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(nettyClientConfig.getConnectTimeoutMillis())) {
                if (cw.isOK()) {
                    log.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture);
                    return cw.getChannel();
                } else {
                    log.error("createChannel: connect remote host[{}] failed, {} ", addr, channelFuture.cause());
                }
            } else {
                log.error("createChannel: connect remote host[{}], {}", addr, channelFuture);
            }
        }
        return null;
    }

    /**
     * netty消息处理器
     * @author errorfatal89@gmail.com
     */
    public class NettyClientHandler extends SimpleChannelInboundHandler<NetworkCommand> {

        /**
         * 处理消息
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
     * netty连接管理
     *
     * @author errorfatal89@gmail.com
     */
    class NettyConnetManageHandler extends ChannelDuplexHandler {

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
                throws Exception {
            final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
            log.info("NETTY CLIENT PIPELINE: CONNECT  {} => {}", local, remote);
            super.connect(ctx, remoteAddress, localAddress, promise);

            if (NettyNetworkClient.this.channelEventListener != null) {
                NettyNetworkClient.this.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remote, ctx.channel()));
            }
        }


        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: DISCONNECT {}", remoteAddress);
            closeChannel(ctx.channel());
            super.disconnect(ctx, promise);

            if (NettyNetworkClient.this.channelEventListener != null) {
                NettyNetworkClient.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }


        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
            log.info("NETTY CLIENT PIPELINE: CLOSE {}", remoteAddress);
            closeChannel(ctx.channel());
            super.close(ctx, promise);

            if (NettyNetworkClient.this.channelEventListener != null) {
                NettyNetworkClient.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
            log.warn("NETTY CLIENT PIPELINE: exceptionCaught {}", remoteAddress);
            log.warn("NETTY CLIENT PIPELINE: exceptionCaught exception.", cause);
            closeChannel(ctx.channel());
            if (NettyNetworkClient.this.channelEventListener != null) {
                NettyNetworkClient.this.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
                    log.warn("NETTY CLIENT PIPELINE: IDLE exception [{}]", remoteAddress);
                    closeChannel(ctx.channel());
                    if (NettyNetworkClient.this.channelEventListener != null) {
                        NettyNetworkClient.this
                                .putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress, ctx.channel()));
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }
    }
}

