package com.github.futurefs.store.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息发布订阅broker
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
@Slf4j
public class DefaultBroker implements Broker {

    private final ConcurrentHashMap<String, PubSubRelation> relTable = new ConcurrentHashMap<>(32);

    private final AtomicInteger threadIdx = new AtomicInteger(0);
    private final ThreadPoolExecutor dispatcher = new ThreadPoolExecutor(4, 4, 60L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), r -> new Thread(r, "broker-dispatcher-" + threadIdx.getAndIncrement()), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 注册消费者
     * @param topicName
     * @param consumer
     */
    @Override
    public void registerConsumer(String topicName, Consumer consumer) {
        Topic topic = new Topic(topicName);
        PubSubRelation relation = relTable.get(topicName);
        if (relation == null) {
            relation = new PubSubRelation();
            relation.setTopic(topic);
        }
        relation.getConsumerList().add(consumer);
    }

    /**
     * 注册生产者
     * @param topicName
     */
    @Override
    public void registerProducer(String topicName, Producer producer) {
        Topic topic = new Topic(topicName);
        PubSubRelation relation = relTable.get(topicName);
        if (relation == null) {
            relation = new PubSubRelation();
            relation.setTopic(topic);
        }
        relation.getProducerList().add(producer);
    }

    @Override
    public void sendMessage(Message message) {
        // 校验topic信息
        String topicName = message.getTopicName();
        if (ObjectUtils.isEmpty(topicName)) {
            throw new IllegalArgumentException("topic name");
        }

        // 获取发布订阅关系
        PubSubRelation relation = relTable.get(topicName);
        if (ObjectUtils.isEmpty(relation)) {
            log.warn("pub/sub relation does not exists, topic: <{}>, we will discard this message", topicName);
        }

        // 并发调度执行
        relation.getConsumerList().forEach(consumer -> {
            dispatcher.execute(() -> consumer.consume(message));
        });

    }
}
