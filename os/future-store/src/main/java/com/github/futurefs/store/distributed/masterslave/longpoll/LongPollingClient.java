package com.github.futurefs.store.distributed.masterslave.longpoll;

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
    private final long time = System.currentTimeMillis();

    /**
     * 事件回调
     */
    private final LongPollCallback callback;

    /**
     * 客户端参数
     */
    private final Object args;

    public LongPollingClient(LongPollCallback callback, Object args) {
        this.callback = callback;
        this.args = args;
    }
}
