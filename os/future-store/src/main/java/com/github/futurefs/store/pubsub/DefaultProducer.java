package com.github.futurefs.store.pubsub;


/**
 * 默认producer
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:23
 */
public class DefaultProducer implements Producer {

    private final Broker broker;

    public DefaultProducer(Broker broker) {
        this.broker = broker;
    }


    @Override
    public void produce(Message message) {
        broker.deliver(message);
    }
}
