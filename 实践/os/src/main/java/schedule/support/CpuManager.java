package schedule.support;

import schedule.PutTaskResult;
import schedule.Scheduler;
import schedule.SchedulerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cpu 管理
 *
 * @author jiquanxi
 * @date 2021/12/31
 */
public class CpuManager {
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    /*
     * Bit field accessors that don't require unpacking ctl.
     * These depend on the bit layout and on workerCount being never negative.
     */

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * Attempts to CAS-increment the workerCount field of ctl.
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * Attempts to CAS-decrement the workerCount field of ctl.
     */
    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }
    /**
     * 锁
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 条件变量
     */
    private final Condition termination = lock.newCondition();

    /**
     * 调度器
     */
    private final Scheduler scheduler;

    /**
     * CPU列表
     */
    private final List<Cpu> cpus;

    /**
     * 核心CPU数量
     */
    private final int coreCpuCount;

    /**
     * 最大CPU数量
     */
    private final int maxCpuCount;

    public CpuManager(int coreCpuCount, int maxCpuCount, int policy) {
        this.coreCpuCount = coreCpuCount;
        this.maxCpuCount = maxCpuCount;
        this.scheduler = SchedulerFactory.getScheduler(policy);
        this.cpus = new ArrayList<>(maxCpuCount);
    }

    public void runTask(Task task) {
        if (task == null) {
            throw new NullPointerException();
        }

        // 所有的任务都尝试先进调度器
        // 如果进调度器失败，那么应该是队列满了
        // 队列满了说明任务占用CPU时间过长，这时需要激活其他CPU进行工作
        // 查看现在正在运行的CPU数量是不是小于coreCpuCount
        // 如果小于等于coreCpuCount，那么直接创建CPU运行任务
        PutTaskResult putTaskResult = scheduler.putTask(task);
        if (putTaskResult.isEnqueued() && !putTaskResult.isMustRunNow()) {
            int c = ctl.get();
            if (workerCountOf(c) < coreCpuCount) {
                // 添加CPU到CPU队列
                addCpu(task, true);
            }

            return;
        } else {
            if (addCpu(task, false)) {
                return;
            }

            // 处理当前没有CPU可以执行情况
            // 如果当前CPU都忙，那么需要中断一个CPU的执行任务
            // 实现方式使用park线程的方式 LockSupport.park();
        }

        throw new RuntimeException("Tasks are stacked.");
    }

    private boolean addCpu(Task task, boolean core) {
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            if (rs >= SHUTDOWN &&
                    !(rs == SHUTDOWN && task == null)) {
                return false;
            }

            int cpuCount = workerCountOf(c);
            if (cpuCount >= (core ? coreCpuCount : maxCpuCount)) {
                return false;
            }

            if (compareAndIncrementWorkerCount(c)) {
                break;
            }

            c = ctl.get();
            if (runStateOf(c) != rs) {
                continue;
            }
        }

        boolean added = false;
        Cpu cpu = new Cpu(0, scheduler, this);
        lock.lock();
        try {
            cpus.add(cpu);
            added = true;
        } finally {
            lock.unlock();
        }

        if (added) {
            cpu.start();
        }
        return true;
    }

    public void removeCpu(Cpu cpu) {
        // 减少CPU正在运行的CPU数量
        decrementWorkerCount();
        lock.lock();
        try {
            cpus.remove(cpu);

            // 检查是否全部cpu都已经执行完成
            if (cpus.isEmpty() && !scheduler.hasTask()) {
                termination.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Decrements the workerCount field of ctl. This is called only on
     * abrupt termination of a thread (see processWorkerExit). Other
     * decrements are performed within getTask.
     */
    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    public void awaitTermination() {
        lock.lock();
        try {
            for (;;) {
                try {
                    termination.await();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
