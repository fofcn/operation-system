package cache.leetcode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * leetcode代码实现
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/07
 */
public class LRUCache {
    private final LruCacheImpl<Integer, Integer> lruCache;
    public LRUCache(int capacity) {
        this.lruCache = new LruCacheImpl(capacity);
    }

    public int get(int key) {
        Integer val = lruCache.get(key);
        if (val == null) {
            return -1;
        }

        return val;
    }

    public void put(int key, int value) {
        lruCache.set(key, value);
    }

    /**
     * 节点value列表
     */
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

        public CacheNode<K, V> moveToFirst(CacheNode<K, V> node) {
            // 只有一个节点或要移动的节点也在第一个，不做任何动作
            if (first == last || node == first) {
                return node;
            }

            CacheNode<K, V> prev = node.getPrev();
            CacheNode<K, V> next = node.getNext();

            node.setNext(first);
            node.setPrev(null);
            first.setPrev(node);
            first = node;

            prev.setNext(next);
            if (next != null) {
                next.setPrev(prev);
            } else {
                last = prev;
            }

            return node;
        }

        public CacheNode<K, V> removeLast() {
            // 情况1：没有节点，空链表
            if (first == null) {
                return null;
            }

            CacheNode<K, V> node;
            // 情况2：只有一个节点
            if (first == last) {
                node = first;
                first = null;
                last = null;
            } else {
                // 情况3： 有两个节点或更多
                node = last;
                CacheNode<K, V> prev = last.getPrev();
                prev.setNext(null);
                last = prev;
            }

            size--;

            return node;
        }

        public int getSize() {
            return size;
        }

        @Override
        public Iterator<CacheNode<K, V>> iterator() {
            return new LinkedList.LinkedListNodeIterator();
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
        }
    }
    /**
     * 缓存节点
     *
     * @author errorfatal89@gmail.com
     * @date 2022/01/07
     */
    class CacheNode<K, V> {
        private K key;

        private V value;

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

    class LruCacheImpl<K, V> {

        private final Map<K, CacheNode<K, V>> indexTable;

        private final LinkedList linkedListNode;

        private final int capacity;


        public LruCacheImpl(int capacity) {
            this.capacity = capacity;
            this.indexTable = new HashMap<>(capacity);
            this.linkedListNode = new LinkedList();
        }

        public void set(K k, V v) {
            // 基本参数检查
            if (k == null || v == null) {
                throw new IllegalArgumentException("K V");
            }

            // 判断当前缓存节点数量是否大于等于capacity
            // 如果大于等于capacity，那么现在就需要踢出链表中最后一个节点
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
        }

        public V get(K k) {
            // 基本参数检查
            if (k == null) {
                return null;
            }

            CacheNode<K, V> foundNode = null;
            foundNode = indexTable.get(k);

            if (foundNode != null) {
                adjustNode(foundNode);
                return foundNode.getValue();
            }

            return null;
        }

        public int size() {
            return indexTable.size();
        }

        public void clear() {
            indexTable.clear();
            linkedListNode.clear();
        }

        private void evictCacheNode(boolean onlyOne) {
            while (linkedListNode.getSize() >= capacity) {
                CacheNode<K, V> removedNode = removeLast();
                if (removedNode != null) {
                    if (onlyOne) {
                        break;
                    }
                }
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

}
