package com.github.futurefs.store.pubsub;

/**
 * 生产者
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
public interface Producer {

    /**
     * 生产消息
     * @param message 消息内容
     */
    void produce(Message message);
}
