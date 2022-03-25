package com.github.futurefs.store.pubsub;

import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 发布订阅关系
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
@Data
public class PubSubRelation {

    /**
     * 主题
     */
    private volatile Topic topic;

    /**
     * 生产者列表
     */
    private volatile CopyOnWriteArrayList<Producer> producerList = new CopyOnWriteArrayList<>();

    /**
     * 消费者列表
     */
    private volatile CopyOnWriteArrayList<Consumer> consumerList = new CopyOnWriteArrayList<>();

    /**
     * 主题消息
     */
    private volatile ArrayBlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(1024);
}
