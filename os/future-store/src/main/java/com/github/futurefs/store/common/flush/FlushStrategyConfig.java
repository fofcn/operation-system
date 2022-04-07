package com.github.futurefs.store.common.flush;

import lombok.Data;

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
    private FlushStrategyEnum strategy;

    /**
     * 写入文件数阈值
     */
    private int flushThreshold;

    /**
     * 写入文件字节数
     */
    private int bytes;
}
