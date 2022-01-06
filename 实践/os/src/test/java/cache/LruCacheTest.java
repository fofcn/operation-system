package cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.StdOut;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LRU缓存算法测试类
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class LruCacheTest {

    private LruCache<Integer, String> lruCache;

    private final int capacity = 10;

    @Before
    public void before() {
        lruCache = new LruCache<>(capacity);
    }

    @Test
    public void testNormalSet() {
        lruCache.set(1, "1");
        Assert.assertEquals(1, lruCache.size());
    }

    @Test
    public void testParallelSet() throws InterruptedException {
        AtomicInteger index = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            for (int i = 0; i < capacity + 1; i++) {
                lruCache.set(i, "" + i);
            }

        });

        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Assert.assertEquals(lruCache.size(), capacity);

        for (int i = 0; i < capacity + 1; i++) {
            StdOut.println("key: " + i + ", value: " + lruCache.get(i));
        }
    }
}
