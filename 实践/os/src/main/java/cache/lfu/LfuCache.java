package cache.lfu;

import cache.Cache;
import cache.CacheNode;
import cache.LinkedList;
import cache.lru.LruCacheNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 最不经常使用缓存算法
 *
 * @author jiquanxi
 * @date 2022/01/10
 */
public class LfuCache<K, V> implements Cache<K, V> {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<K, LfuCacheNode<K, V>> indexTable;
    private final Map<Integer, LfuCacheNode<K, V>> freqMap;
    private final LinkedList dataList;
    private final int capacity;

    public LfuCache(int capacity) {
        this.capacity = capacity;
        this.indexTable = new HashMap<>(capacity);
        this.freqMap = new HashMap<>(capacity);
        this.dataList = new LinkedList();
    }

    @Override
    public void set(K k, V v) {
        // 基本参数检查
        if (k == null || v == null) {
            throw new IllegalArgumentException("K V");
        }

        lock.writeLock().lock();
        try {
            LfuCacheNode<K, V> node = indexTable.get(k);
            if (node == null) {
                // 如果当前缓存节点大小超过了容量，则执行删除
                if (size() >= capacity) {
                    evictCacheNode(true);
                }

                node = new LfuCacheNode<>(k, v);
                indexTable.put(k, node);

                // todo 添加到计数节点
            } else {
                node.setValue(v);
                // todo 更新计数
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K k) {
        lock.readLock().lock();
        try {
            LfuCacheNode<K, V> node = indexTable.get(k);
            // todo 更新计数
            return node == null ? null : node.getValue();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {

        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evictCacheNode(boolean onlyOne) {
        lock.writeLock().lock();
        try {

        } finally {
            lock.writeLock().unlock();
        }
    }
}
