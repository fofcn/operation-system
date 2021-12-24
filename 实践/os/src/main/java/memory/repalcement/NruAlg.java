package memory.repalcement;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Not recently used algorithm 最近未使用算法
 * 算法介绍(Introduce to the Not recently used algorithm)：
 * 当一个缓存内容被引用时，引用位R位标记为1表示被引用。同样的，当一个缓存内容被修改，那么设置M位为1表示被修改
 *
 * 时钟中断说明(Introduce to the Time Interrupt):
 * 时钟中断在固定的时间周期发生，当中断发生时，时钟中断处理程序会将所有页面的引用位设置为0表示未引用。
 *
 * 当缓存满时，算法将所有的缓存分类为四类
 * -------------------------------
 * 等级 | 引用位      | 修改位
 * -------------------------------
 * 0   |    0       |     0      |  未引用，未修改
 * 1   |    0       |     1      |  未引用，已修改
 * 2   |    1       |     0      |  已引用，未修改
 * 3   |    1       |     1      |  已引用，已修改
 * -------------------------------
 * @author jiquanxi
 * @date 2021/12/24
 */
public class NruAlg {
    /**
     * 一个时钟中断
     */
    private final ScheduledThreadPoolExecutor timeInterrupt = new ScheduledThreadPoolExecutor(1,
            r -> new Thread(r, "nru_time_interrupt"),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * R位表示读
     */
    private volatile int rBit;

    /**
     * M位表示写
     */
    private volatile int mBit;


}
