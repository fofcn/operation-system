package schedule.support;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 线程队列
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/31
 */
public class TaskQueue extends ArrayBlockingQueue<Task> {
    public TaskQueue(int capacity) {
        super(capacity);
    }
}
