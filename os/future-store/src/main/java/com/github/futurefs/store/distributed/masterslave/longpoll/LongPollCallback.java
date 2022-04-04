package com.github.futurefs.store.distributed.masterslave.longpoll;

/**
 * 长轮询回调
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/03 23:58
 */
public interface LongPollCallback<Data, Args> {

    /**
     * 收到新数据
     * @param data
     * @param args
     */
    void onNewData(Data data, Args args);

    /**
     * 超时
     * @param args 客户端参数
     */
    void onTimeout(Args args);
}
