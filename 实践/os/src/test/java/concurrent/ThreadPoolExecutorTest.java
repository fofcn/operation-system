package concurrent;

import org.junit.Assert;
import org.junit.Test;
import util.StdOut;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程
 */
public class ThreadPoolExecutorTest {

    @Test
    public void testThreadCountNotGreaterThanCoreSize() throws InterruptedException {
        AtomicInteger pooledThreadIdx = new AtomicInteger(0);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                3,
                60L * 1000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10),
                r -> new Thread(r, "executor-tester" + pooledThreadIdx.getAndIncrement()),
                new ThreadPoolExecutor.AbortPolicy()
        );

        AtomicBoolean pass = new AtomicBoolean(false);

        // 向线程池提交12个任务
        submitTask(threadPoolExecutor, pass, 11);

        Thread controlThread = new Thread(() -> {
            int i = 0;
            while (i++ < 10) {
                sleep(1000);
                Assert.assertEquals(1, threadPoolExecutor.getActiveCount());
                StdOut.println("thread pool running workers: " + threadPoolExecutor.getActiveCount());
            }

            pass.set(true);

            i = 0;

            while (i++ < 10) {
                sleep(200);
                StdOut.println("thread pool running workers: " + threadPoolExecutor.getActiveCount());
            }

            Assert.assertEquals(0, threadPoolExecutor.getActiveCount());
            StdOut.println("thread pool running workers: " + threadPoolExecutor.getActiveCount());
        });
        controlThread.start();
        controlThread.join();
    }

    private void submitTask(ThreadPoolExecutor threadPoolExecutor, AtomicBoolean pass, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            threadPoolExecutor.submit(() -> {
                while (!pass.get()) {
                    StdOut.println(Thread.currentThread().getName() + ": Thread running..." );
                    sleep(1000);
                }
            });
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
