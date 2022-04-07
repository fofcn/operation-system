package com.github.futurefs.store.common.flush;

import com.github.futurefs.netty.thread.PoolHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步刷盘
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/06 23:56
 */
@Slf4j
public class AsyncFlushStrategy implements FlushStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ThreadPoolExecutor flushThread = PoolHelper.newSingleThreadPool("flushStrategy", "flush-thread-", 1024);

    private final CountDownLatchReset single = new CountDownLatchReset(1);

    private final FileChannel fileChannel;

    private final int writeCntThreshold;

    public AsyncFlushStrategy(FileChannel fileChannel, int writeCntThreshold) {
        this.fileChannel = fileChannel;
        this.writeCntThreshold = writeCntThreshold;
        start();
    }

    @Override
    public void flush() {
        if (counter.incrementAndGet() > writeCntThreshold) {
            single.countDown();
            counter.set(0);
        }
    }

    private void start() {
        this.flushThread.execute(() -> {
            for (; ;) {
                try {
                    single.await();
                    fileChannel.force(false);
                } catch (InterruptedException e) {
                    log.error("", e);
                } catch (IOException e) {
                    log.error("flush error", e);
                }
            }
        });
    }
}
