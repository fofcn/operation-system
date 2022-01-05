package concurrent;

import org.junit.Assert;
import org.junit.Before;
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
    /**
     * 线程池中线程索引
     */
    private final AtomicInteger pooledThreadIdx = new AtomicInteger(0);

    /**
     * 核心线程数
     */
    private final int coreSizeN = 1;

    /**
     * 最大线程数
     */
    private final int maxSizeN = 3;

    /**
     * 任务最大排队数量
     */
    private final int queueSizeM = 10;

    /**
     * 线程池中大于coreSize的线程空闲时间，单位：毫秒
     */
    private final long keepAliveTime = 60L * 1000;

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 控制线程池任务执行开关
     */
    private final AtomicBoolean pass = new AtomicBoolean(false);

    @Before
    public void before() {
        // 新建线程池
        threadPoolExecutor = new ThreadPoolExecutor(
                coreSizeN,
                maxSizeN,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                // 任务队列为最大排队为10的任务队列
                new ArrayBlockingQueue<>(queueSizeM),
                // 定制ThreadFactory，定义线程名称，以在多个线程池场景下区分业务线程
                r -> new Thread(r, "executor-tester-" + pooledThreadIdx.getAndIncrement()),

                // 如果排队数量超过10，且线程最大已经达到maximumPoolSize时，再有任务提交时的拒绝策略
                // 一般是直接拒绝：表示服务仅能支撑这么多
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 测试在全部任务终止后，再次向线程池提交任务
     * @throws InterruptedException
     */
    @Test
    public void testAfterTerminatedReputTask() throws InterruptedException {
        // 向线程池提交n + m个任务
        submitTask(threadPoolExecutor, pass, maxSizeN + queueSizeM);
        startControlThread(pass, maxSizeN, threadPoolExecutor);
        StdOut.println("-----阶段1执行完成--------");
        StdOut.println("-----线程池任务队列大小--------:  " + threadPoolExecutor.getQueue().size());
        StdOut.println("-----重新提交新任务--------");
        // 向线程池提交m个任务
        // 如果向线程池提交超过m个任务可能会报错（注意是可能呦，因为你在提交任务的时候任务的过程中线程池中活动的线程可能已经消费了）
        // 因为此时线程池的活动线程数量为n个，在提交新任务的时候不会直接创建新的线程执行
        // 而是任务入队，如果超过任务队列大小m，则会执行执行拒绝策略抛出异常
        pass.set(false);
        submitTask(threadPoolExecutor, pass, queueSizeM);
        startControlThread(pass, maxSizeN, threadPoolExecutor);
    }

    @Test
    public void testRejectTask() throws InterruptedException {
        // 向线程池提交n + m + 1个任务
        submitTask(threadPoolExecutor, pass, maxSizeN + queueSizeM + 1);
        startControlThread(pass, maxSizeN, threadPoolExecutor);
    }

    /**
     * 测试线程池线程数量等于最大线程数用例
     * @throws InterruptedException
     */
    @Test
    public void testThreadCountGreaterThanCoreSize() throws InterruptedException {
        // 向线程池提交n + m个任务
        submitTask(threadPoolExecutor, pass, maxSizeN + queueSizeM);
        startControlThread(pass, maxSizeN, threadPoolExecutor);
    }

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
        startControlThread(pass, 1, threadPoolExecutor);
    }

    /**
     * 提交任务到线程池
     * @param threadPoolExecutor 线程池
     * @param pass 任务控制开关
     * @param taskCount 任务数量
     */
    private void submitTask(ThreadPoolExecutor threadPoolExecutor, AtomicBoolean pass, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            threadPoolExecutor.execute(() -> {
                while (!pass.get()) {
                    StdOut.println(Thread.currentThread().getName() + ": Thread running..." );
                    sleep(1000);
                }
            });
        }
    }

    /**
     * 睡眠
     * @param millis 睡眠时间，单位：毫秒
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制线程池中任务执行的线程
     * @param pass 任务执行开关 true:任务执行，false：任务睡眠
     * @param maxSizeN  线程池中最大线程数量
     * @param threadPoolExecutor 线程池
     * @throws InterruptedException
     */
    private void startControlThread(AtomicBoolean pass, int maxSizeN, ThreadPoolExecutor threadPoolExecutor) throws InterruptedException {
        // 控制线程
        Thread controlThread = new Thread(() -> {
            int i = 0;
            while (i++ < 10) {
                // 先将自己睡眠一秒防止线程池还没有“反应过来”就获取活动线程数量为0的问题
                sleep(1000);

                // 睡眠一秒后再获取活动的线程数量应该为1,
                Assert.assertEquals(maxSizeN, threadPoolExecutor.getActiveCount());
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

}
