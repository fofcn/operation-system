package com.github.futurefs.store.cache.outprocess;

import com.github.futurefs.store.cache.Cache;

/**
 * redis缓存实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:49:00
 */
public class RedisCacheImpl<Key, Val> implements Cache<Key, Val> {

    @Override
    public Val get(Key key) {
        return null;
    }

    @Override
    public void set(Key key, Val val) {

    }
}
