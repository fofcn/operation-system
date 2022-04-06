package com.github.futurefs.store.common.flush;

import lombok.Data;

import java.nio.channels.FileChannel;

/**
 * 刷盘配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/06 23:58
 */
@Data
public class FlushStrategyConfig {

    /**
     * 刷盘策略枚举
     */
    private FlushStrategyEnum strategyEnum;

    /**
     * 文件channel
     */
    private FileChannel channel;

    /**
     * 写入文件数阈值
     */
    private int flushThreshold;
}
