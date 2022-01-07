package cache;

import util.StdOut;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LRU算法cache
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class LruCache<K extends Comparable, V> implements Cache<K, V> {

    private final int evictThreshold = 75;

    private final AtomicInteger curSize = new AtomicInteger(0);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ThreadPoolExecutor evictThreadPool = new ThreadPoolExecutor(1, 1,
            60L* 1000,
            TimeUnit.MILLISECONDS,
            // 任务队列为最大排队为100的任务队列
            new ArrayBlockingQueue<>(100),
            // 定制ThreadFactory，定义线程名称，以在多个线程池场景下区分业务线程
            r -> new Thread(r, "executor-tester-0"),

            // 如果排队数量超过100，且线程最大已经达到maximumPoolSize时，再有任务提交时的拒绝策略
            // 一般是直接拒绝：表示服务仅能支撑这么多
            new ThreadPoolExecutor.AbortPolicy());

    private volatile boolean evictStart = true;

    private final CountDownLatchReset resetCountDown = new CountDownLatchReset(1);

    private final ConcurrentHashMap<K, CacheNode<K, V>> table;

    private final LinkedListNode linkedListNode;

    private final int capacity;

    private final int evictCount;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.table = new ConcurrentHashMap<>(capacity);
        this.evictCount = (evictThreshold * capacity) / 100;
        this.linkedListNode = new LinkedListNode();

        this.evictThreadPool.execute(() -> {
            while (evictStart) {
                try {
                    resetCountDown.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                StdOut.println("start time thread to evict nodes");
                evictCacheNode(false);
                resetCountDown.reset();
            }


        });
    }

    @Override
    public void set(K k, V v) {
        // 基本参数检查
        if (k == null || v == null) {
            throw new IllegalArgumentException("K V");
        }

        // 新建缓存节点
        CacheNode<K, V> cacheNode = new CacheNode<>(k, v);

        // 判断当前缓存节点数量是否大于等于capacity
        // 如果大于等于capacity，那么现在就需要踢出链表中最后一个节点
        if (curSize.get() >= capacity) {
            evictCacheNode(true);
        }

        lock.writeLock().lock();
        try {
            // 根据key获取Value链表
            CacheNode<K, V> existsNode = table.get(k);
            table.put(k, cacheNode);

            if (existsNode != null) {
                existsNode.value = v;
                linkedListNode.moveToFirst(existsNode);
            } else {
                linkedListNode.addFirst(cacheNode);
                // 当前缓存容量加1
                curSize.incrementAndGet();
            }
        } finally {
            lock.writeLock().unlock();
        }

        // 查看当前缓存大小,如果缓存大小超过了剔除的阈值，那么就执行剔除策略
        if (curSize.get() >= evictCount) {
            // 启动线程执行剔除
            StdOut.println("signal to evict nodes");
            resetCountDown.countDown();
        }
    }

    @Override
    public V get(K k) {
        // 基本参数检查
        if (k == null) {
            return null;
        }

        V val = null;
        lock.readLock().lock();
        try {
            CacheNode<K, V> foundNode = table.get(k);
            if (foundNode != null) {
                linkedListNode.moveToFirst(foundNode);
                val = foundNode.value;
            }
        } finally {
            lock.readLock().unlock();
        }


        return val;
    }

    @Override
    public int size() {
        return curSize.get();
    }

    @Override
    public void clear() {

    }

    private void evictCacheNode(boolean onlyOne) {
        StdOut.println("start evict nodes");
        int delCount = 0;

        lock.writeLock().lock();
        try {
            int shouldDelCount = linkedListNode.getSize() - capacity / 2;
            if (shouldDelCount <= 0) {
                return;
            }
            CacheNode<K, V> node;
            if (onlyOne) {
                node = linkedListNode.removeLast();
                table.remove(node.key);
                delCount++;
            } else {
                while (shouldDelCount-- >= 0) {
                    node = linkedListNode.removeLast();
                    table.remove(node.key);
                    delCount++;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }

        StdOut.println("end evict nodes, total deleted nodes: " + delCount);

    }

    private class CountDownLatchReset {
        private final class Sync extends AbstractQueuedSynchronizer {
            private static final long serialVersionUID = 4982264981922014374L;

            private final int count;

            Sync(int count) {
                this.count = count;
                setState(count);
            }

            int getCount() {
                return getState();
            }

            @Override
            protected int tryAcquireShared(int acquires) {
                return (getState() == 0) ? 1 : -1;
            }

            @Override
            protected boolean tryReleaseShared(int releases) {
                // Decrement count; signal when transition to zero
                for (;;) {
                    int c = getState();
                    if (c == 0) {
                        return false;
                    }

                    int nextc = c-1;
                    if (compareAndSetState(c, nextc)) {
                        return nextc == 0;
                    }
                }
            }

            void resetState() {
                for (;;) {
                    int c = getState();
                    if (c == 0) {
                        if (compareAndSetState(c, count)) {
                            break;
                        }
                    } else {
                        break;
                    }

                }
            }
        }

        private final CountDownLatchReset.Sync sync;

        public CountDownLatchReset(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("count < 0");
            }
            this.sync = new CountDownLatchReset.Sync(count);
        }

        public void await() throws InterruptedException {
            sync.acquireSharedInterruptibly(1);
        }

        public boolean await(long timeout, TimeUnit unit)
                throws InterruptedException {
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        }

        public void countDown() {
            sync.releaseShared(1);
        }

        public void reset() {
            sync.resetState();
        }

        public long getCount() {
            return sync.getCount();
        }

        @Override
        public String toString() {
            return super.toString() + "[Count = " + sync.getCount() + "]";
        }
    }

    /**
     * 节点value列表
     */
    private class LinkedListNode implements Iterable<CacheNode<K, V>>{

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
            first.prev = null;
            if (oldFirst == null) {
                last = first;
            } else {
                first.next = oldFirst;
                oldFirst.prev = first;
            }

            size++;
        }

        public void moveToFirst(CacheNode<K, V> node) {
            if (first == null) {
                throw new NoSuchElementException("");
            }

            CacheNode<K, V> prev = node.prev;
            CacheNode<K, V> next = node.next;

            node.next = first;
            node.prev = null;
            first = node;

            if (prev != null && next != null) {
                prev.next = next;
                next.prev = prev;
            } else if (prev != null && next == null) {
                prev.next = null;
                last = prev;
            }

        }

        public CacheNode<K, V> removeLast() {
            // 情况1：没有节点，空链表
            if (first == null) {
                throw new NoSuchElementException("");
            }

            CacheNode<K, V> node = null;
            // 情况2：只有一个节点
            if (first.next == null) {
                node = first;
                first = null;
                last = null;
            } else {
                // 情况3： 有两个节点或更多
                node = last;
                CacheNode<K, V> prev = last.prev;
                last.prev = prev;
                prev.next = null;
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
                cur = cur.next;
                return tmp;
            }
        }
    }

    private class CacheNode<K, V> {
        private final K key;

        private V value;

        private CacheNode<K, V> prev;

        private CacheNode<K, V> next;


        public CacheNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
