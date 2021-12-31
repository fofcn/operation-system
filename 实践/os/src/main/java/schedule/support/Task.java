package schedule.support;

/**
 * 线程
 *
 * @author jiquanxi
 * @date 2021/12/31
 */
public interface Task {

    /**
     * 执行
     */
    void run();

    /**
     * 获取线程ID
     * @return 线程ID
     */
    int getThreadId();

    /**
     * 获取到达时间
     * @return 到达时间
     */
    long getArriveTime();

    /**
     * 获取开始执行时间
     * @return
     */
    long getStartTime();

    /**
     * 设置开始执行时间
     * @param startTime
     */
    void setStartTime(long startTime);

    /**
     * 获取结束执行时间
     * @return
     */
    long getEndTime();

    /**
     * 设置结束执行时间
     * @param endTime
     */
    void setEndTime(long endTime);
}
