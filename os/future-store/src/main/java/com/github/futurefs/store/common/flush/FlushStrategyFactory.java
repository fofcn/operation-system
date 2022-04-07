package com.github.futurefs.store.common.flush;

import java.nio.channels.FileChannel;

/**
 * 刷盘工厂
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/06 23:57
 */
public interface FlushStrategyFactory {

    /**
     * 创建刷盘策略
     * @param config 刷盘策略配置
     * @param fileChannel 文件管道
     * @return 刷盘策略
     */
    FlushStrategy createStrategy(FlushStrategyConfig config, FileChannel fileChannel);
}
