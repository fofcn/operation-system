package cache.lru;

import cache.CacheNode;

/**
 * LRU缓存节点
 *
 * @author jiquanxi
 * @date 2022/01/10
 */
public class LruCacheNode<K, V> extends CacheNode<K, V> {

    public LruCacheNode(K key, V value) {
        super(key, value);
    }
}
