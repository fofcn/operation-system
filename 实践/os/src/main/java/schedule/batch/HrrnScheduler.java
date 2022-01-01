package schedule.batch;

import schedule.PutTaskResult;
import schedule.Scheduler;
import schedule.support.Task;

public class HrrnScheduler implements Scheduler {
    @Override
    public PutTaskResult putTask(Task task) {
        return null;
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
