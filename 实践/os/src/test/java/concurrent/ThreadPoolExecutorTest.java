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
 * ThreadPoolExecutor测试类
 */
public class ThreadPoolExecutorTest {

    @Test
    public void testThreadCountNotGreaterThanCoreSize() throws InterruptedException {
        // 线程池中线程索引
        AtomicInteger pooledThreadIdx = new AtomicInteger(0);

        // 新建线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                3,
                60L * 1000,
                TimeUnit.MILLISECONDS,
                // 任务队列为最大排队为10的任务队列
                new ArrayBlockingQueue<>(10),
                // 定制ThreadFactory，定义线程名称，以在多个线程池场景下区分业务线程
                r -> new Thread(r, "executor-tester" + pooledThreadIdx.getAndIncrement()),

                // 如果排队数量超过10，且线程最大已经达到maximumPoolSize时，再有任务提交时的拒绝策略
                // 一般是直接拒绝：表示服务仅能支撑这么多
                new ThreadPoolExecutor.AbortPolicy()
        );

        AtomicBoolean pass = new AtomicBoolean(false);

        // 向线程池提交11个任务
        submitTask(threadPoolExecutor, pass, 11);

        // 控制线程
        Thread controlThread = new Thread(() -> {
            int i = 0;
            while (i++ < 10) {
                // 先将自己睡眠一秒防止线程池还没有“反应过来”就获取活动线程数量为0的问题
                sleep(1000);

                // 睡眠一秒后再获取活动的线程数量应该为1,
                Assert.assertEquals(1, threadPoolExecutor.getActiveCount());
                StdOut.println("thread pool running workers: " + threadPoolExecutor.getActiveCount());
            }

            // 将线程中的任务全部放行
            pass.set(true);

            i = 0;
            // 等待大约2秒时间再判断线程池中的活动线程数量应该为0
            // 因为任务已经执行完成了
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
