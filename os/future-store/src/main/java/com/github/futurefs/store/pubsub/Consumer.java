package com.github.futurefs.store.pubsub;

/**
 * 消费者
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
public interface Consumer {

    /**
     * 消费消息
     * @param message 消息
     */
    void consume(Message message);
}
