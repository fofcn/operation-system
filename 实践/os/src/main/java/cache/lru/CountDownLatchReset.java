package cache.lru;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 可重置信号量
 *
 * @author jiquanxi
 * @date 2022/01/07
 */
public class CountDownLatchReset {
    private final class Sync extends AbstractQueuedSynchronizer {

        private final int count;

        Sync(int count) {
            this.count = count;
            setState(count);
        }

        int getCount() {
            return getState();
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (; ; ) {
                int c = getState();
                if (c == 0) {
                    return false;
                }

                int nextc = c - 1;
                if (compareAndSetState(c, nextc)) {
                    return nextc == 0;
                }
            }
        }

        void resetState() {
            for (; ; ) {
                int c = getState();
                if (c == 0) {
                    if (compareAndSetState(c, count)) {
                        break;
                    }
                } else {
                    break;
                }

            }
        }
    }

    private final Sync sync;

    public CountDownLatchReset(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0");
        }
        this.sync = new Sync(count);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    public void reset() {
        sync.resetState();
    }

    public long getCount() {
        return sync.getCount();
    }

    @Override
    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
