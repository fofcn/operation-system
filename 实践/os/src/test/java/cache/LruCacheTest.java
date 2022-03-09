package cache;

import cache.lru.LruCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.StdOut;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * LRU缓存算法测试类
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/06
 */
public class LruCacheTest {

    private LruCache<Integer, String> lruCache;

    private final int capacity = 100;

    @Before
    public void before() {
        StdOut.println("new lru cache");
        lruCache = new LruCache<>(capacity);
    }

    @Test
    public void testNormalSet() {
        lruCache.set(1, "1");
        Assert.assertEquals(1, lruCache.size());
    }

    @Test
    public void testNormalSetAndGet() {
        for (int i = 0; i < capacity; i++) {
            lruCache.set(i, "" + i);
        }

        for (int i = 0; i < capacity; i++) {
            String val = lruCache.get(i);
            StdOut.println("Key: " + i + ", value: " + lruCache.get(i));
            Assert.assertEquals("" + i, val);
        }
    }


    @Test
    public void testRemoveFrontByGet() {
        for (int i = 0; i < capacity; i++) {
            lruCache.set(i, "" + i);
        }

        // 第一次获取key为10
        // 缓存顺序应该为10,0,...

    }

    @Test
    public void testOverrideFromEnd() {
        int overrideCount = 10;
        for (int i = 0; i < capacity + overrideCount; i++) {
            if (capacity == i) {
                StdOut.println("");
            }

            lruCache.set(i, "" + i);
        }

        for (int i = 0; i < capacity + overrideCount; i++) {
            String val = lruCache.get(i);
            StdOut.println("Key: " + i + ", value: " + lruCache.get(i));
            if (i < overrideCount) {
                Assert.assertNull(val);
            } else {
                Assert.assertEquals("" + i, val);
            }
        }
    }

    @Test
    public void testNormalParallelSetAndGet() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(capacity * 2);
        IntStream.range(0, capacity).<Runnable>mapToObj(key -> () -> {
                lruCache.set(key, UUID.randomUUID().toString());
            countDownLatch.countDown();
        }).forEach(executorService::execute);

        IntStream.range(0, capacity).<Runnable>mapToObj(key -> () -> {
                lruCache.get(key);
            countDownLatch.countDown();
        }).forEach(executorService::execute);
        countDownLatch.await();

        StdOut.println("LruCache Size: " + lruCache.size());
        Assert.assertEquals(lruCache.size(), capacity);

        for (int i = 0; i < capacity; i++) {
            StdOut.println("key: " + i + ", value: " + lruCache.get(i));
        }
    }

    @Test
    public void testEvictParallelSetAndGet() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(capacity * 2);
        IntStream.range(0, capacity * 2).<Runnable>mapToObj(key -> () -> {
            lruCache.set(key, UUID.randomUUID().toString());
            lruCache.set(key, UUID.randomUUID().toString());
            countDownLatch.countDown();
        }).forEach(executorService::execute);

        IntStream.range(capacity, capacity * 2).<Runnable>mapToObj(key -> () -> {
            StdOut.println(lruCache.get(key));
            countDownLatch.countDown();
        }).forEach(executorService::execute);
        countDownLatch.await();

        StdOut.println("LruCache Size: " + lruCache.size());
        for (int i = capacity; i < capacity * 2; i++) {
            StdOut.println("key: " + i + ", value: " + lruCache.get(i));
        }
        Assert.assertEquals(lruCache.size(), capacity);

        lruCache.clear();
        Assert.assertEquals(lruCache.size(), 0);
        executorService.shutdown();
    }

}
