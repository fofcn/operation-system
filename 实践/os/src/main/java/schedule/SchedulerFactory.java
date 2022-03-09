package schedule;

import schedule.batch.FcfsScheduler;

/**
 * 调度器工厂
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/31
 */
public class SchedulerFactory {

    public static Scheduler getScheduler(int policy) {
        if (policy == SchedulerPolicy.FCFS) {
            return new FcfsScheduler();
        }

        return null;
    }
}
