package com.github.futurefs.store.pubsub;

/**
 * 主题消息
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
public interface Message {

    /**
     * 获取主题名称
     * @return 主题名称
     */
    String getTopicName();

    /**
     * 获取优先级
     * @return
     */
    default int getPriority() {return 0;}
}
