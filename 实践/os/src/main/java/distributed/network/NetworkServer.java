package distributed.network;

import distributed.network.exception.NetworkConnectException;
import distributed.network.exception.NetworkSendRequestException;
import distributed.network.exception.NetworkTimeoutException;
import distributed.network.interceptor.RequestInterceptor;
import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;

public interface NetworkServer {

    void start();

    void shutdown();

    /**
     * 注册消息处理器
     * @param requestCode 命令码
     * @param processor 消息处理
     * @param executorService 消息线程池（可为空，为空使用默认线程池）
     */
    void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executorService);

    /**
     * 注册消息拦截器
     * @param interceptor
     */
    void registerInterceptor(RequestInterceptor interceptor);

    /**
     * 同步发送消息（发送完成后，等待应答）
     * @param channel channel通道
     * @param request 请求命令
     * @param timeoutMillis 超时时间
     * @return
     * @throws InterruptedException
     * @throws NetworkTimeoutException
     * @throws NetworkSendRequestException
     * @throws NetworkConnectException
     */
    NetworkCommand sendSync(Channel channel, NetworkCommand request, long timeoutMillis)
            throws InterruptedException,
            NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException;

    /**
     * one way消息发送
     * @param channel channel通道
     * @param request 请求命令
     * @param timeoutMillis 超时时间
     * @param sendCallback 发送回调
     */
    void sendOneway(Channel channel, NetworkCommand request, long timeoutMillis, SendCallback sendCallback)
            throws InterruptedException,
            NetworkTimeoutException, NetworkSendRequestException, NetworkConnectException;
}
