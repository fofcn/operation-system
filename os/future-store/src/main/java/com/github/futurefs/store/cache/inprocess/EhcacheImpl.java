package com.github.futurefs.store.cache.inprocess;

import com.github.futurefs.store.cache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * EhCache缓存实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:49:00
 */
public class EhcacheImpl<Key, Val> implements Cache<Key, Val> {

    public EhcacheImpl() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                                ResourcePoolsBuilder.heap(100))
                                .build())
                .build(true);


    }

    @Override
    public Val get(Key key) {
        return null;
    }

    @Override
    public void set(Key key, Val val) {

    }
}
