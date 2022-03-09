package distributed.network.netty;

import io.netty.channel.Channel;

/**
 * 通道事件监听器
 */
public interface ChannelEventListener {

    /**
     * 连接
     * @param remoteAddr 地址
     * @param channel 通道
     */
    void onChannelConnect(String remoteAddr, Channel channel);

    /**
     * 关闭
     * @param remoteAddr 地址
     * @param channel 通道
     */
    void onChannelClose(String remoteAddr, Channel channel);

    /**
     * 异常
     * @param remoteAddr 地址
     * @param channel 通道
     */
    void onChannelException(String remoteAddr, Channel channel);

    /**
     * 空闲
     * @param remoteAddr 地址
     * @param channel 通道
     */
    void onChannelIdle(String remoteAddr, Channel channel);
}
