package com.github.futurefs.store.pubsub;

/**
 * 消息发布订阅broker
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
public interface Broker {
    /**
     * 注册消费者
     * @param topic 主题名称
     * @param consumer 消费者
     */
    void registerConsumer(String topic, Consumer consumer);

    /**
     * 注册生产者
     * @param topic 主题名称
     * @param producer 生产者
     */
    void registerProducer(String topic, Producer producer);

    /**
     * 投递消息
     * @param message 消息内容
     */
    void deliver(Message message);

    /**
     * 停止broker
     */
    void stop();
}
