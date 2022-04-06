package com.github.futurefs.store.pubsub;

import com.github.futurefs.netty.thread.PoolHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 发布订阅关系
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
@Data
@Slf4j
public class PubSubRelation {

    public static final AtomicInteger RELATION_IDX = new AtomicInteger(0);

    /**
     * 主题
     */
    private volatile Topic topic;

    /**
     * 消息分发是否启动
     */
    private final AtomicBoolean start = new AtomicBoolean(true);

    /**
     * 生产者列表
     */
    private volatile CopyOnWriteArrayList<Producer> producerList = new CopyOnWriteArrayList<>();

    /**
     * 消费者列表
     */
    private volatile CopyOnWriteArrayList<Consumer> consumerList = new CopyOnWriteArrayList<>();

    /**
     * 分发线程池
     */
    private final ThreadPoolExecutor deliverPool = PoolHelper.newFixedPool("brokerPubSub" + RELATION_IDX.getAndIncrement(), "broker-pub-sub", 2, 1024);

    /**
     * 主题消息
     */
    private volatile PriorityBlockingQueue<Message> messageQueue = new PriorityBlockingQueue<>(1024, new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getPriority() - o2.getPriority();
        }
    });

    public void deliver() {
        deliverPool.execute(() -> {
            Message msg = null;
            while (start.get()) {
                try {
                    if ((msg = messageQueue.take()) != null) {
                        Message finalMsg = msg;
                        consumerList.forEach(consumer -> {
                            deliverPool.execute(() -> {
                                consumer.consume(finalMsg);
                            });
                        });
                    }
                } catch (InterruptedException e) {
                    log.error("", e);
                }

            }
        });
    }

    public void stop() {
        start.set(false);
    }
}
