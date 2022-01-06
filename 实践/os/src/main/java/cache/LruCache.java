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

/**
 * LRU算法cache
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class LruCache<K extends Comparable, V> implements Cache<K, V> {

    private final int evictThreshold = 75;

    private final AtomicInteger curSize = new AtomicInteger(0);

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

    private final ConcurrentHashMap<K, LinkedListNode> table;

    private final int capacity;

    private final int evictCount;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.table = new ConcurrentHashMap<>(capacity);
        this.evictCount = (evictThreshold * capacity) / 100;

        this.evictThreadPool.execute(() -> {
            evictCacheNode();
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
        // 如果大于

        // 根据key获取Value链表
        LinkedListNode listNode = table.get(k);
        synchronized (k) {
            // 如果链表为空，那么新建链表
            if (listNode == null) {
                listNode = new LinkedListNode();
                table.put(k, listNode);
            }

            // 如果链表不为空，那么获取当前链表
            // 将缓存内容加入到链表头
            listNode.addFirst(cacheNode);
        }

        // 当前缓存容量加1
        curSize.incrementAndGet();

        // 查看当前缓存大小,如果缓存大小超过了剔除的阈值，那么就执行剔除策略
        if (curSize.get() >= evictCount) {
            // 启动线程执行剔除
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
        CacheNode<K, V> foundNode = null;

        // 根据key获取value链表
        LinkedListNode listNode = table.get(k);
        synchronized (k) {
            for (CacheNode<K, V> cacheNode : listNode) {
                if (cacheNode.key.equals(k)) {
                    val = cacheNode.value;
                    foundNode = cacheNode;
                    break;
                }
            }

            // 将获取到的value移动到链表头
            if (foundNode != null) {
                listNode.moveToFirst(foundNode);
            }
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

    private void evictCacheNode() {
        while (evictStart) {
            try {
                resetCountDown.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int delCount = 0;
            StdOut.println("start evict nodes");
            for (Map.Entry<K, LinkedListNode> entry : table.entrySet()) {
                synchronized (entry.getKey()) {
                    LinkedListNode listNode = entry.getValue();
                    if (listNode == null) {
                        continue;
                    }

                    int delSize = listNode.getSize() / 2;
                    while (delSize-- != 0) {
                        listNode.removeLast();
                    }
                    delCount += delSize;
                }
            }
            StdOut.println("end evict nodes, total deleted nodes: " + delCount);
            resetCountDown.reset();
        }

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
                first = last;
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

        public void removeLast() {
            // 情况1：没有节点，空链表
            if (first == null) {
                throw new NoSuchElementException("");
            }

            // 情况2：只有一个节点
            if (first.next == null) {
                first = null;
                last = null;
            } else {
                // 情况3： 有两个节点或更多
                CacheNode<K, V> prev = last.prev;
                last.prev = prev;
                prev.next = null;
            }

            size--;
        }

        public int getSize() {
            return size;
        }

        @Override
        public Iterator<CacheNode<K, V>> iterator() {
            return new LinkedListNodeIterator();
        }

        private class LinkedListNodeIterator implements Iterator<CacheNode<K, V>> {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public CacheNode<K, V> next() {
                return null;
            }
        }
    }

    private class CacheNode<K, V> {
        private final K key;

        private final V value;

        private CacheNode<K, V> prev;

        private CacheNode<K, V> next;


        public CacheNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
