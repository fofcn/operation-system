package cache.lru;

import cache.Cache;
import util.StdOut;

import java.util.Iterator;

/**
 * 节点value列表
 */
class LinkedListNode<K, V> implements Iterable<CacheNode<K, V>> {

    private int size;

    private CacheNode<K, V> first;

    private CacheNode<K, V> last;

    public LinkedListNode() {
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
        if (prev == null) {
            StdOut.println();
        }
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
    }
}
