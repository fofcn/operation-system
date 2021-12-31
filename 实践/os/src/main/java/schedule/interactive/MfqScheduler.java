package schedule.interactive;

import schedule.Scheduler;
import schedule.support.Task;

public class MfqScheduler implements Scheduler {
    @Override
    public boolean putTask(Task task) {
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
