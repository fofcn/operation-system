package cache.leetcode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * leetcode lfu 缓存算法实现
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/11
 */
public class LFUCache {

    private final LfuCache<Integer, Integer> lfuCache;

    public LFUCache(int capacity) {
        this.lfuCache = new LfuCache(capacity);
    }

    public int get(int key) {
        Integer val = lfuCache.get(key);
        if (val == null) {
            return -1;
        }

        return val;
    }

    public void put(int key, int value) {
        lfuCache.set(key, value);
    }


    class LfuCache<K, V> {
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

        public void set(K k, V v) {
            // 基本参数检查
            if (k == null || v == null) {
                throw new IllegalArgumentException("K V");
            }

            if (capacity == 0) {
                return;
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
                    indexTable.put(k, node);

                    LfuCacheNodeList list = freqMap.get(node.getFrequency());
                    if (list == null) {
                        list = new LfuCacheNodeList(node.getFrequency());
                        if (first == null || first.getFrequency() != 1) {
                            first = list;
                        }
                    }

                    freqMap.put(node.getFrequency(), list);
                    list.addLast(node);
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

        public int size() {
            lock.lock();
            try {
                return indexTable.size();
            } finally {
                lock.unlock();
            }
        }

        public void clear() {
            lock.lock();
            try {
                indexTable.clear();
                // todo 清空链表
                freqMap.clear();
            } finally {
                lock.unlock();
            }
        }

        private void evictCacheNode(boolean onlyOne) {
            LfuCacheNodeList list = first;
            if (list == null) {
                return;
            }
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
                if (list == first) {
                    first = nextList;
                }
            }

            indexTable.put(k, node);
        }
    }
    class LinkedList<K, V> implements Iterable<CacheNode<K, V>> {

        private int size;

        private CacheNode<K, V> first;

        private CacheNode<K, V> last;

        public LinkedList() {
            this.first = null;
            this.last = null;
            this.size = 0;
        }

        public void addFirst(CacheNode<K, V> node) {
            CacheNode<K, V> oldFirst = first;
            first = node;
            first.setPrev(null);
            if (oldFirst == null) {
                last = first;
            } else {
                first.setNext(oldFirst);
                oldFirst.setPrev(first);
            }

            size++;
        }

        public void addLast(CacheNode<K, V> node) {
            // 记录老的last节点
            CacheNode<K, V> oldLast = last;

            // 将last节点直接指向新节点
            last = node;

            // 链表初始化 如果last为空，那么代表链表还没有新元素，则将first指向last进行
            if (oldLast == null) {
                first = last;
            } else {
                // 如果链表已经进行了初始化，那么设置last的上一个节点是老的last节点
                last.setPrev(oldLast);

                // 将老的last节点的下一个节点设置为最新last节点
                oldLast.setNext(last);
            }

            size++;
        }

        /**
         * 删除任意节点
         * @param node 待删除的节点
         * @return 待删除的节点
         */
        public CacheNode<K, V> remove(CacheNode<K, V> node) {
            // 分情况删除
            // 情况1：前驱节点不存在 node.prev == null 为头节点，需要处理头节点
            // 情况2：前驱节点存在   node.prev ！= null 可能为尾节点，可能为中间节点
            // 情况3：后继节点不存在 node.next == null 确定为尾节点
            // 情况4：后继节点存在 node.next != null 可能为头结点，可能为中间节点
            CacheNode<K, V> prev = node.getPrev();
            CacheNode<K, V> next = node.getNext();
            if (prev == null) {
                first = next;
            } else {
                prev.setNext(next);
                node.setPrev(null);
            }

            if (next == null) {
                last = prev;
            } else {
                next.setPrev(prev);
                node.setNext(null);
            }

            size--;

            return node;
        }

        /**
         * 将节点移动到第一个节点
         * @param node 待移动节点
         * @return 待移动节点
         */
        public CacheNode<K, V> moveToFirst(CacheNode<K, V> node) {
            if (node == null) {
                throw new IllegalArgumentException();
            }

            // 先删除掉这个节点
            node = remove(node);

            // 再将该节点添加到第一个上面
            addFirst(node);
            return node;
        }

        /**
         * 删除最后一个节点
         * @return 删除前的最后一个节点
         */
        public CacheNode<K, V> removeFirst() {
            CacheNode<K, V> tmpFirst = first;
            CacheNode<K, V> next = tmpFirst.getNext();
            first = next;
            if (next == null) {
                last = null;
            } else {
                next.setPrev(null);
            }

            size--;

            return tmpFirst;
        }

        public CacheNode<K, V> removeLast() {
            CacheNode<K, V> tmpLast = last;
            CacheNode<K, V> prev = tmpLast.getPrev();
            last = prev;
            if (prev == null) {
                first = null;
            } else {
                prev.setNext(null);
            }

            size--;

            return tmpLast;
        }

        public int getSize() {
            return size;
        }

        @Override
        public Iterator<CacheNode<K, V>> iterator() {
            return new LinkedListNodeIterator();
        }

        public void clear() {
            CacheNode<K, V> cur = first;
            while (cur != null) {
                CacheNode<K, V> tmp = cur.getNext();
                cur.setKey(null);
                cur.setValue(null);
                cur.setPrev(null);
                cur.setNext(null);
                cur = tmp;
            }
            size = 0;
        }

        public CacheNode<K, V> getLast() {
            return last;
        }

        public CacheNode<K,V> getFirst() {
            return first;
        }

        private class LinkedListNodeIterator implements Iterator<CacheNode<K, V>> {
            private CacheNode<K, V> cur;

            public LinkedListNodeIterator() {
                this.cur = first;
            }

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public CacheNode<K, V> next() {
                CacheNode<K, V> tmp = cur;
                cur = cur.getNext();
                return tmp;
            }

            @Override
            public void remove() {

            }
        }
    }

    class LfuCacheNode<K, V> extends CacheNode<K, V> {

        private int frequency;

        public LfuCacheNode(K key, V value) {
            this(key, value, 1);
        }

        public LfuCacheNode(K key, V value, int frequency) {
            super(key, value);
            this.frequency = frequency;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return "LfuCacheNode{" +
                    "frequency=" + frequency +
                    ", key=" + key +
                    ", value=" + value +
                    '}';
        }
    }
    class LfuCacheNodeList<K, V> {

        private final int frequency;

        private LinkedList list;

        private LfuCacheNodeList prev;

        private LfuCacheNodeList next;

        public LfuCacheNodeList(int frequency) {
            this.frequency = frequency;
            this.list = new LinkedList();
        }

        public LfuCacheNode<K, V> addLast(LfuCacheNode<K, V> node) {
            list.addLast(node);
            return node;
        }

        public int getFrequency() {
            return frequency;
        }

        public LfuCacheNodeList getPrev() {
            return prev;
        }

        public void setPrev(LfuCacheNodeList prev) {
            this.prev = prev;
        }

        public LfuCacheNodeList getNext() {
            return next;
        }

        public void setNext(LfuCacheNodeList next) {
            this.next = next;
        }

        public int size() {
            return list.getSize();
        }

        public <V, K> LfuCacheNodeList promote(LfuCacheNodeList prevList, LfuCacheNodeList nextList, LfuCacheNode<K,V> node) {
            if (nextList == null) {
                nextList = new LfuCacheNodeList(node.getFrequency());
                nextList.setPrev(prevList);
            }

            nextList.addLast(node);
            return nextList;
        }

        public <V, K> void remove(LfuCacheNode<K,V> node) {
            list.remove(node);
        }

        public CacheNode removeFirst() {
            return list.removeFirst();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("LfuCacheNodeList{" +
                    "frequency=" + frequency +
                    '}');
            for (Object node : list) {
                builder.append(node);
            }

            return builder.toString();
        }
    }
    class CacheNode<K, V> {
        protected K key;

        protected V value;

        private CacheNode<K, V> prev;

        private CacheNode<K, V> next;

        public CacheNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public CacheNode<K, V> getPrev() {
            return prev;
        }

        public void setPrev(CacheNode<K, V> prev) {
            this.prev = prev;
        }

        public CacheNode<K, V> getNext() {
            return next;
        }

        public void setNext(CacheNode<K, V> next) {
            this.next = next;
        }

    }

}
