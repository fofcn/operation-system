package com.github.futurefs.store.distributed.masterslave.longpoll;

/**
 * 长轮询客户端上下文
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/03 23:59
 */
public interface LongPollClientContext {

    /**
     * 添加属性
     * @param key
     * @param value
     */
    void addAttr(Object key, Object value);

    /**
     * 获取属性
     * @param key
     * @return
     */
    Object getAttr(Object key);
}
