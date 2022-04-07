package com.github.futurefs.store.common.flush;

import java.nio.channels.FileChannel;

/**
 * 刷盘工厂实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/07 00:01
 */
public class DefaultFlushStrategyFactory implements FlushStrategyFactory {

    @Override
    public FlushStrategy createStrategy(FlushStrategyConfig config, FileChannel fileChannel) {
        if (config.getStrategy().equals(FlushStrategyEnum.ASYNC)) {
            return new AsyncFlushStrategy(fileChannel, config.getFlushThreshold());
        } else {
            return new SyncFlushStrategy(fileChannel);
        }

    }
}
