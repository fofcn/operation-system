package scheduler.support;

import org.junit.Test;
import schedule.SchedulerPolicy;
import schedule.support.CpuManager;
import schedule.support.Task;
import util.StdOut;

/**
 * CPU管理测试类
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/31
 */
public class CpuManagerTest {

    @Test
    public void testSingleCpuTask() throws InterruptedException {
        CpuManager cpuManager = new CpuManager(1, 1, SchedulerPolicy.FCFS);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            Task task = new Task() {
                @Override
                public void run() {
                    StdOut.println("task number: [" + finalI + "]");
                }

                @Override
                public int getThreadId() {
                    return 0;
                }

                @Override
                public long getArriveTime() {
                    return 0;
                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public void setStartTime(long startTime) {

                }

                @Override
                public long getEndTime() {
                    return 0;
                }

                @Override
                public void setEndTime(long endTime) {

                }
            };
            cpuManager.runTask(task);

        }

        cpuManager.awaitTermination();

        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            Task task = new Task() {
                @Override
                public void run() {
                    StdOut.println("task number: [" + finalI + "]");
                }

                @Override
                public int getThreadId() {
                    return 0;
                }

                @Override
                public long getArriveTime() {
                    return 0;
                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public void setStartTime(long startTime) {

                }

                @Override
                public long getEndTime() {
                    return 0;
                }

                @Override
                public void setEndTime(long endTime) {

                }
            };
            cpuManager.runTask(task);
        }

        cpuManager.awaitTermination();
    }

    @Test
    public void testTwoCpuTask() throws InterruptedException {
        CpuManager cpuManager = new CpuManager(2, 2, SchedulerPolicy.FCFS);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            Task task = new Task() {
                @Override
                public void run() {
                    StdOut.println("task number: [" + finalI + "]");
                }

                @Override
                public int getThreadId() {
                    return 0;
                }

                @Override
                public long getArriveTime() {
                    return 0;
                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public void setStartTime(long startTime) {

                }

                @Override
                public long getEndTime() {
                    return 0;
                }

                @Override
                public void setEndTime(long endTime) {

                }
            };
            cpuManager.runTask(task);

        }

        cpuManager.awaitTermination();

        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            Task task = new Task() {
                @Override
                public void run() {
                    StdOut.println("task number: [" + finalI + "]");
                }

                @Override
                public int getThreadId() {
                    return 0;
                }

                @Override
                public long getArriveTime() {
                    return 0;
                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public void setStartTime(long startTime) {

                }

                @Override
                public long getEndTime() {
                    return 0;
                }

                @Override
                public void setEndTime(long endTime) {

                }
            };
            cpuManager.runTask(task);
        }

        cpuManager.awaitTermination();
    }
}
