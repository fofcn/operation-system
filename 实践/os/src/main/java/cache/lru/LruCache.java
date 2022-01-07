package cache.lru;

import cache.Cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LRU算法cache
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class LruCache<K, V> implements Cache<K, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ConcurrentHashMap<K, CacheNode<K, V>> indexTable;

    private final LinkedList linkedListNode;

    private final int capacity;


    public LruCache(int capacity) {
        this.capacity = capacity;
        this.indexTable = new ConcurrentHashMap<>(capacity);
        this.linkedListNode = new LinkedList();
    }

    @Override
    public void set(K k, V v) {
        // 基本参数检查
        if (k == null || v == null) {
            throw new IllegalArgumentException("K V");
        }

        // 判断当前缓存节点数量是否大于等于capacity
        // 如果大于等于capacity，那么现在就需要踢出链表中最后一个节点
        lock.writeLock().lock();
        try {
            // 根据key获取Value链表
            CacheNode<K, V> cachedNode = indexTable.get(k);
            // 节点存在则更新节点的值
            if (cachedNode != null) {
                adjustNode(cachedNode);
                cachedNode.setValue(v);
            } else {
                if (linkedListNode.getSize() >= capacity) {
                    evictCacheNode(true);
                }

                CacheNode<K, V> first = linkedListNode.getFirst();
                // 节点不存在则新建缓存节点，放入链表和链表索引中
                cachedNode = new CacheNode<>(k, v);
                linkedListNode.addFirst(cachedNode);
                updateIndex(first);
                updateIndex(cachedNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V get(K k) {
        // 基本参数检查
        if (k == null) {
            return null;
        }

        CacheNode<K, V> foundNode = null;
        lock.readLock().lock();
        try {
            foundNode = indexTable.get(k);
        } finally {
            lock.readLock().unlock();
        }

        if (foundNode != null) {
            lock.writeLock().lock();
            try {
                adjustNode(foundNode);
            } finally {
                lock.writeLock().unlock();
            }

            return foundNode.getValue();
        }

        return null;
    }

    @Override
    public int size() {
        return indexTable.size();
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            indexTable.clear();
            linkedListNode.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void evictCacheNode(boolean onlyOne) {
        lock.writeLock().lock();
        try {
            while (linkedListNode.getSize() >= capacity) {
                CacheNode<K, V> removedNode = removeLast();
                if (removedNode != null) {
                    if (onlyOne) {
                        break;
                    }
                }
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    private CacheNode<K, V> removeLast() {
        CacheNode<K, V> node = linkedListNode.removeLast();
        if (node != null) {
            indexTable.remove(node.getKey());
            CacheNode<K, V> last = linkedListNode.getLast();
            if (last != null) {
                updateIndex(last);
            }
        }
        return node;
    }

    private void updateIndex(CacheNode<K, V> node) {
        if (node != null) {
            indexTable.put(node.getKey(), node);
        }
    }

    private CacheNode<K, V> adjustNode(CacheNode<K, V> node) {
        if (node == null) {
            return null;
        }

        CacheNode<K, V> first = linkedListNode.getFirst();
        CacheNode<K, V> last = linkedListNode.getLast();
        CacheNode<K, V> prev = node.getPrev();
        CacheNode<K, V> next = node.getNext();

        node = linkedListNode.moveToFirst(node);

        updateIndex(first);
        updateIndex(last);
        updateIndex(prev);
        updateIndex(next);
        updateIndex(node);
        return node;
    }

}
