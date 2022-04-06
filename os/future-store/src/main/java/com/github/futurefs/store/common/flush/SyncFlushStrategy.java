package com.github.futurefs.store.common.flush;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 同步刷盘
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/06 23:55
 */
@Slf4j
public class SyncFlushStrategy implements FlushStrategy {

    private final FileChannel fileChannel;

    public SyncFlushStrategy(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    @Override
    public void flush() {
        try {
            fileChannel.force(false);
        } catch (IOException e) {
            log.error("flush error");
        }
    }
}
