package distributed.network.netty;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import distributed.network.common.Pair;
import distributed.network.common.ServiceThread;
import distributed.network.enums.ResponseCode;
import distributed.network.exception.NetworkConnectException;
import distributed.network.exception.NetworkRequestNotMatchedException;
import distributed.network.exception.NetworkSendRequestException;
import distributed.network.exception.NetworkTimeoutException;
import distributed.network.interceptor.RequestInterceptor;
import distributed.network.processor.NettyRequestProcessor;
import distributed.network.util.NetworkUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author errorfatal89@gmail.com
 */
public abstract class NettyNetworkAbstract {
    private static final Logger log = LoggerFactory.getLogger("network");
    protected final ConcurrentHashMap<Integer, ResponseFuture> responseTable =
            new ConcurrentHashMap<Integer, ResponseFuture>(256);

    /**
     * 消息队列处理
     */
    protected final Map<Integer, Pair<NettyRequestProcessor, ExecutorService>> processorTable =
            new HashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>>();

    private final ScheduledExecutorService responseTableScanner = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Response Table Scanner"));

    protected final List<RequestInterceptor> interceptors = new ArrayList<RequestInterceptor>(10);

    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();

    protected volatile SslContext sslContext;

    static{
        ParserConfig.getGlobalInstance().setAsmEnable(false);
        SerializeConfig.getGlobalInstance().setAsmEnable(false);
    }

    /**
     * 扫描超时等待响应的请求，并删除
     */
    public void scanResponseTable() {
        responseTableScanner.scheduleAtFixedRate(() -> {
            try {
                Iterator<Map.Entry<Integer, ResponseFuture>> iterator = responseTable.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, ResponseFuture> next = iterator.next();
                    if (next.getValue().getBeginTimestamp() + 3000 < System.currentTimeMillis()) {
                        responseTable.remove(next.getKey());
                    }
                }

            } catch (Throwable e) {
                log.error("scanResponseTable exception", e);
            }
        }, 3 * 1000, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 注册消息处理器
     * @param code
     * @param processor
     */
    protected void registerProcessor(int code, NettyRequestProcessor processor, ExecutorService executorService) {

    }

    public void processMessageReceived(ChannelHandlerContext ctx, NetworkCommand msg) throws Exception {
        log.info("recv message: [{}]", msg);
        final NetworkCommand cmd = msg;
        if (cmd != null) {
            switch (cmd.getType()) {
                case NetworkCommand.ONE_WAY_TYPE:
                case NetworkCommand.REQUEST_TYPE:
                    processRequestCommand(ctx, cmd);
                    break;
                case NetworkCommand.RESPONSE_TYPE:
                    processResponseCommand(ctx, cmd);
                    break;
                default:
                    break;
            }
        }
    }

    public void putNettyEvent(NettyEvent event) {
        this.nettyEventExecutor.putNettyEvent(event);
    }

    public void sendOnewayImpl(final Channel channel, final NetworkCommand request, final long timeoutMillis)
            throws InterruptedException,
            NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException {
        request.markOnewayType();
        try {
            channel.writeAndFlush(request).addListener((ChannelFutureListener) f -> {
                if (!f.isSuccess()) {
                    log.warn("send a request command to channel <" + channel.remoteAddress() + "> failed.");
                }
            });
        } catch (Exception e) {
            log.warn("write send a request command to channel <" + channel.remoteAddress() + "> failed.");
            throw new NetworkSendRequestException(NetworkUtil.parseChannelRemoteAddr(channel), e);
        }
    }

    /**
     * 同步发送消息
     * @param channel 通道
     * @param request 请求命令
     * @param timeoutMillis 超时时间
     * @return
     * @throws InterruptedException
     * @throws NetworkTimeoutException
     * @throws NetworkSendRequestException
     */
    public NetworkCommand sendSyncImpl(Channel channel, NetworkCommand request, long timeoutMillis)
            throws InterruptedException, NetworkTimeoutException, NetworkSendRequestException {
        final int sequenceId = request.getSequenceId();
        try {
            final ResponseFuture responseFuture = new ResponseFuture(sequenceId, timeoutMillis);
            this.responseTable.put(sequenceId, responseFuture);
            final SocketAddress addr = channel.remoteAddress();

            if (!channel.isActive()) {
                return null;
            }

            channel.writeAndFlush(request).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    responseFuture.setSendRequestOK(true);
                    return;
                } else {
                    responseFuture.setSendRequestOK(false);
                }

                responseTable.remove(sequenceId);
                responseFuture.setCause(f.cause());
                responseFuture.putResponse(null);
                log.error("send a request command to channel <{}> failed.", addr);
            });

            NetworkCommand responseCommand = responseFuture.waitResponse(timeoutMillis);
            if (null == responseCommand) {
                if (responseFuture.isSendRequestOK()) {
                    throw new NetworkTimeoutException(NetworkUtil.parseSocketAddressAddr(addr), timeoutMillis);
                } else {
                    throw new NetworkSendRequestException(NetworkUtil.parseSocketAddressAddr(addr), responseFuture.getCause());
                }
            }

            return responseCommand;
        } finally {
            responseTable.remove(sequenceId);
        }
    }

    protected abstract ChannelEventListener getChannelEventListener();

    /**
     * 处理消息
     * @param ctx
     * @param command
     * @throws Exception
     */
    private void processRequestCommand(final ChannelHandlerContext ctx, final NetworkCommand command) throws Exception {
        final Pair<NettyRequestProcessor, ExecutorService> matchedPair = processorTable.get(command.getCode());
        if (matchedPair == null) {
            throw new NetworkRequestNotMatchedException("Request code: " + command.getCode() + " not matched");
        }

        Runnable task = () -> {
            try {
                NetworkCommand response = null;
                boolean isFilterPass = true;
                if (interceptors != null && !interceptors.isEmpty()) {
                    for (RequestInterceptor interceptor : interceptors) {
                        boolean result = interceptor.doBeforeRequest(NetworkUtil.parseChannelRemoteAddr(ctx.channel()),
                                command);
                        if (!result) {
                            isFilterPass = false;
                            break;
                        }
                    }
                }

                if (!isFilterPass) {
                    response = NetworkCommand.createResponseCommand(null);
                    response.setCode(ResponseCode.INTERCEPTOR_FAILED.getCode());
                } else {
                    response = matchedPair.getK().processRequest(ctx, command);
                    if (response != null && !command.isOnewayType()) {
                        if (interceptors != null && !interceptors.isEmpty()) {
                            for (RequestInterceptor interceptor : interceptors) {
                                interceptor.doAfterResponse(NetworkUtil.parseChannelRemoteAddr(ctx.channel()),
                                        command, response);
                            }
                        }

                        response.setSequenceId(command.getSequenceId());
                        response.markResponseType();
                        ctx.writeAndFlush(response);
                    }
                }

            } catch (Throwable e) {
                log.error("process request exception", e);
                log.error(command.toString());
            }
        };

        matchedPair.getV().submit(task);
    }

    private void processResponseCommand(ChannelHandlerContext ctx, NetworkCommand msg) {
        int sequence = msg.getSequenceId();
        ResponseFuture responseFuture = responseTable.get(sequence);
        if (responseFuture != null) {
            responseFuture.setResponseCommand(msg);
            responseFuture.release();
            responseTable.remove(sequence);
        }
    }

    protected void shutdown() {
        responseTableScanner.shutdown();
    }


    static class ChannelWrapper {
        private final ChannelFuture channelFuture;

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }


        public boolean isOK() {
            return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
        }


        public boolean isWriteable() {
            return this.channelFuture.channel().isWritable();
        }


        public Channel getChannel() {
            return this.channelFuture.channel();
        }


        public ChannelFuture getChannelFuture() {
            return channelFuture;
        }
    }

    class NettyEventExecutor extends ServiceThread {
        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<NettyEvent>();
        private final int maxSize = 10000;

        public void putNettyEvent(final NettyEvent event) {
            if (this.eventQueue.size() <= maxSize) {
                this.eventQueue.add(event);
            } else {
                log.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }

        @Override
        public void run() {
            log.info(this.getServiceName() + " service started");

            final ChannelEventListener listener = NettyNetworkAbstract.this.getChannelEventListener();

            while (!this.isStopped()) {
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;

                        }
                    }
                } catch (Exception e) {
                    log.warn(this.getServiceName() + " service has exception. ", e);
                }
            }

            log.info(this.getServiceName() + " service end");
        }

        @Override
        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }
    }

}
