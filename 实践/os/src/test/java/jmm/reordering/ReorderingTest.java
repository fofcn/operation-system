package jmm.reordering;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 重排序测试
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/12
 */
public class ReorderingTest {

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    public void testSeeTmp() {
        for (int i = 0; i < 1000_000; i++) {
            Global global = new Global();
            Thread writer = new Thread(() -> {
                global.s = "/tmp/usr".substring(4);
            });

            Thread reader = new Thread(() -> {
                String myS = global.s;
                if ("/tmp".equals(myS)) {
                    System.out.println(myS);
                }

            });

            reader.start();
            writer.start();

        }
    }

    @Test
    public void testWhatCanTheySee() throws InterruptedException {
        for (int i = 0; i < 1000_000; i++) {
            Reordering reordering = new Reordering();
            Thread writer = new Thread(() -> {
                reordering.writer();
            });

            Thread reader = new Thread(() -> {
                reordering.reader();
                if (reordering.getY() == 2 && reordering.getX() == 0) {
                    System.out.println(reordering.toString());
                }

            });

            reader.start();
            writer.start();

            writer.join();
            reader.join();
        }

    }

    private class Global {
        public String s;
    }
}
