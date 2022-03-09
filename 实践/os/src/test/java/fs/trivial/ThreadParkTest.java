package fs.trivial;

import org.junit.Test;
import util.StdOut;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 挂起线程测试
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/31
 */
public class ThreadParkTest {
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 60 * 1000,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10),
            new ThreadFactory() {
                private AtomicInteger threadIdx = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "cpu" + threadIdx.getAndIncrement());
                }
            });

    private volatile boolean parkOne = false;
    private volatile boolean parkTwo= false;

    @Test
    public void testPark() {

        Thread thread1 = new Thread(() -> {
            while (true) {
                StdOut.println("this is thread one.");
                if (parkOne) {
                    LockSupport.park();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            while (true) {
                StdOut.println("this is thread two.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();

        // 10秒后挂起线程1
        Thread parkOneThread = new Thread(() -> {
            int i = 10;
            while (i >= 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
            parkOne = true;
            StdOut.println("park thread one");
            i = 10;
            while (i > 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
            StdOut.println("Unpark thread one");
            parkOne = false;
            LockSupport.unpark(thread1);
        });

        parkOneThread.start();

        try {
            thread1.join();
            thread2.join();
            parkOneThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
