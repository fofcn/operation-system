package com.github.futurefs.store.cache;

/**
 * 缓存
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:44:00
 */
public interface Cache<Key, Val> {

    /**
     * 获取缓存内容
     * @param key 缓存键
     * @return 存在返回值，否则返回空
     */
    Val get(Key key);

    /**
     * 设置键值缓存
     * @param key 键
     * @param val 值
     */
    void set(Key key, Val val);
}
