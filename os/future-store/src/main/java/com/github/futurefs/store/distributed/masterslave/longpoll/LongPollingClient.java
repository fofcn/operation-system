package com.github.futurefs.store.distributed.masterslave.longpoll;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * 长轮询客户端
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/03 23:44
 */
@Data
public class LongPollingClient {
    /**
     * 加入时间
     */
    private long time = System.currentTimeMillis();

    /**
     * 请求偏移
     */
    private long offset;

    /**
     * 请求上下文
     */
    private ChannelHandlerContext ctx;
}
