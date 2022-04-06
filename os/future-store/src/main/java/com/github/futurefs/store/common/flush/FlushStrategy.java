package com.github.futurefs.store.common.flush;

/**
 * 刷盘策略
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/06 23:54
 */
public interface FlushStrategy {
    /**
     * 刷盘
     */
    void flush();
}
