package schedule.batch;

import schedule.Scheduler;
import schedule.support.Task;

public class SjfScheduler implements Scheduler {
    @Override
    public boolean putTask(Task task) {
        // 非抢占式
        // 最短任务优先，这里实现为
        // 队列使用优先级队列，所有的任务必须实现任务时间排序的实现

        // 抢占式
        // 当新任务进入后发现该任务运行最短，那么立即暂停当前执行的任务
        // 将当前任务放入到队列合适的位置
        // 调用新任务执行
        return false;
    }

    @Override
    public Task getTask() {
        return null;
    }

    @Override
    public boolean hasTask() {
        return false;
    }
}
