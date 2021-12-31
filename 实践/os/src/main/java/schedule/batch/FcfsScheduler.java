package schedule.batch;

import schedule.Scheduler;
import schedule.support.Task;
import util.StdOut;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * First Come First Service Scheduling algorithm
 * @author jiquanxi
 * @date
 */
public class FcfsScheduler implements Scheduler {

    private final BlockingQueue<Task> taskQueue;

    public FcfsScheduler() {
        this.taskQueue = new ArrayBlockingQueue<>(2000);
    }
    @Override
    public boolean putTask(Task task) {
        boolean offerResult = taskQueue.offer(task);
        if (!offerResult) {
            StdOut.println("queue failed");
            return false;
        }

        StdOut.println("enqueue size: [" + taskQueue.size() + "]");
        return true;
    }

    @Override
    public Task getTask() {
        try {
            return taskQueue.poll(1 * 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean hasTask() {
        return taskQueue.size() > 0;
    }
}
