package schedule;

import schedule.support.Task;

/**
 * Process Scheduler 进程调度
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/24
 */
public interface Scheduler {

    /**
     * 放置任务，根据调度算法进行任务重排
     * @param task
     */
    PutTaskResult putTask(Task task);

    /**
     * 从调度器获取一个任务
     * @return 任务
     */
    Task getTask();

    /**
     * 检查是否还有任务
     * @return true还有，false：没有了
     */
    boolean hasTask();
}
