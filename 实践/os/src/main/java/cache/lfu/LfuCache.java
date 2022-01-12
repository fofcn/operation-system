package cache.lfu;

import cache.Cache;
import cache.CacheNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 最不经常使用缓存算法
 *
 * @author jiquanxi
 * @date 2022/01/10
 */
public class LfuCache<K, V> implements Cache<K, V> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<K, LfuCacheNode<K, V>> indexTable;
    private final Map<Integer, LfuCacheNodeList<K, V>> freqMap;
    private final int capacity;
    private LfuCacheNodeList first;

    public LfuCache(int capacity) {
        this.capacity = capacity;
        this.indexTable = new HashMap<>(capacity);
        this.freqMap = new HashMap<>(capacity);
    }

    @Override
    public void set(K k, V v) {
        // 基本参数检查
        if (k == null || v == null) {
            throw new IllegalArgumentException("K V");
        }

        lock.lock();
        try {
            LfuCacheNode<K, V> node = indexTable.get(k);
            if (node == null) {
                // 如果当前缓存节点大小超过了容量，则执行删除
                if (size() == capacity) {
                    evictCacheNode(true);
                }

                node = new LfuCacheNode<>(k, v);
                LfuCacheNodeList list = freqMap.get(node.getFrequency());
                if (list == null) {
                    list = new LfuCacheNodeList(node.getFrequency());
                    if (first == null || first.getFrequency() != 1) {
                        first = list;
                    }
                }

                freqMap.put(node.getFrequency(), list);
                node = list.addLast(node);
                indexTable.put(k, node);
            } else {
                // 根据计数获取列表
                // 从当前列表中删除
                // 如果list的数量为空，从map中删除列表
                // 如果list的数量不为空，则不处从map中删除列表
                doPromote(k, v, node);
            }

        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(K k) {
        lock.lock();
        try {
            LfuCacheNode<K, V> node = indexTable.get(k);
            if (node != null) {
                doPromote(k, node.getValue(), node);
                indexTable.put(k, node);
                return node.getValue();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return indexTable.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            indexTable.clear();
            for (Map.Entry<Integer, LfuCacheNodeList<K, V>> entry : freqMap.entrySet()) {
                LfuCacheNodeList list = entry.getValue();
                CacheNode node = null;
                do {
                    node = list.removeFirst();
                } while (node != null);
            }
            freqMap.clear();
        } finally {
            lock.unlock();
        }
    }

    private void evictCacheNode(boolean onlyOne) {
        LfuCacheNodeList list = first;
        CacheNode node = list.removeFirst();
        if (list.size() == 0) {
            first = list.getNext();
            list.setNext(null);
            freqMap.remove(list.getFrequency());
        }
        indexTable.remove(node.getKey());
    }

    private void doPromote(K k, V v, LfuCacheNode<K, V> node) {
        // 从计数map中获取链表，并从链表中删除数据
        int frequency = node.getFrequency();
        LfuCacheNodeList list = freqMap.get(frequency);
        list.remove(node);

        // 节点计数更新
        node.setFrequency(node.getFrequency() + 1);
        // 从下一个计数map中获取下一个计数列表
        LfuCacheNodeList nextList = freqMap.get(node.getFrequency());
        // 将节点放入到下一个节点列表
        if (nextList == null) {
            nextList = new LfuCacheNodeList(node.getFrequency());
        }
        nextList.addLast(node);
        freqMap.put(node.getFrequency(), nextList);
        node.setValue(v);

        // 前一个frequency map中的链表已经没有数据，那么我们就更新first指针
        if (list.size() == 0) {
            list.setPrev(null);
            list.setNext(null);
            freqMap.remove(frequency);
            // 更新first指针
            // 更新条件： 如果要删除的list==first，则更新first为next
            if (list == first) {
                first = nextList;
            }
        }

        indexTable.put(k, node);
    }
}
