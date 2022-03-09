package cache.lfu;

import cache.Cache;
import cache.CacheNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 最不经常使用缓存算法
 *
 * @author errorfatal89@gmail.com
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
        this.indexTable = new ConcurrentHashMap<>(capacity);
        this.freqMap = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public void set(K k, V v) {
        // 基本参数检查
        if (k == null || v == null) {
            throw new IllegalArgumentException("K V");
        }

        lock.lock();
        try {
            // 尝试从缓存索引表中查找key是否存在
            LfuCacheNode<K, V> node = indexTable.get(k);
            // 不存在则检查缓存容量是否超过了最大容量
            if (node == null) {
                // 如果当前缓存节点大小超过了容量，则执行删除
                if (size() == capacity) {
                    evictCacheNode(true);
                }

                // 新建一个缓存节点并将缓存节点添加到LFU双向链表中
                node = new LfuCacheNode<>(k, v);
                LfuCacheNodeList list = freqMap.get(node.getFrequency());
                if (list == null) {
                    list = new LfuCacheNodeList(node.getFrequency());
                    if (first == null || first.getFrequency() != 1) {
                        first = list;
                    }
                }

                // 将缓存节点添加到frequency表中
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
            // 从索引表获取缓存节点
            // 如果缓存节点存在那么就提升缓存节点的计数，并将节点添加到下一个计数链表中
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
        // 直接从最小计数链表中删除第一个
        LfuCacheNodeList list = first;
        CacheNode node = list.removeFirst();

        // 如果删除完成后该计数链表没有缓存节点，则将计数节点删除
        // 这里没有更新first，容量满时触发删除节点导致first更新时，说明有计数为1的节点要加入到frequency表
        if (list.size() == 0) {
            first = list.getNext();
            list.setNext(null);
            freqMap.remove(list.getFrequency());
        }
        // 将索引表中的缓存节点删除
        indexTable.remove(node.getKey());
    }

    private void doPromote(K k, V v, LfuCacheNode<K, V> node) {
        // 从frequency表中获取链表，并从链表中删除数据
        int frequency = node.getFrequency();
        LfuCacheNodeList list = freqMap.get(frequency);
        list.remove(node);

        // 节点计数更新
        node.setFrequency(node.getFrequency() + 1);
        // 从下一个frequency表中获取下一个计数列表
        LfuCacheNodeList nextList = freqMap.get(node.getFrequency());
        // 将节点放入到下一个节点列表
        if (nextList == null) {
            nextList = new LfuCacheNodeList(node.getFrequency());
        }
        nextList.addLast(node);
        freqMap.put(node.getFrequency(), nextList);
        node.setValue(v);

        // 前一个frequency表中的链表已经没有数据，那么我们就更新first指针
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
