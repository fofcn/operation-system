package schedule.support;

import schedule.Scheduler;
import util.StdOut;

/**
 * CPU
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/31
 */
public class Cpu {
    private static final int RUNNING = 1;

    private static final int WAITING = 2;

    private final Runnable r;

    private final Scheduler scheduler;

    private final CpuManager cpuManager;

    private volatile Thread thread;

    private volatile int state;

    public Cpu(int cpuNumber, Scheduler scheduler, CpuManager cpuManager) {
        this.cpuManager = cpuManager;
        this.scheduler = scheduler;
        state = WAITING;

        r = () -> {
            state = RUNNING;
            StdOut.println("cpu 0 is running.");
            Task task;
            while ((task = this.scheduler.getTask()) != null) {
                task.setStartTime(System.currentTimeMillis());
                try {
                    task.run();
                } finally {
                    task.setEndTime(System.currentTimeMillis());
                }
            }

            state = WAITING;
            cpuManager.removeCpu(this);
        };
        thread = new Thread(r, "cpu" + cpuNumber);
    }

    public Thread getThread() {
        return thread;
    }

    public void start() {
        thread.start();
    }
}
