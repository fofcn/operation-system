package jmm.reordering;


import java.util.concurrent.ConcurrentHashMap;

/**
 * 重排序示例
 *
 * @author jiquanxi
 * @date 2022/01/12
 * @see <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html"></>
 */
public class Reordering {
    int x = 0, y = 0;
    public void writer() {
        x = 1;
        y = 2;
    }

    public void reader() {
        int r1 = y;
        int r2 = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Reordering{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
