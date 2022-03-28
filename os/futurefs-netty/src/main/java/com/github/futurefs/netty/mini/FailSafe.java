package com.github.futurefs.netty.mini;

/**
 * 失败重试
 *
 * @author errorfatal89@gmail.com
 */
public final class FailSafe {
    private FailSafe() {

    }

    /**
     * 获取重试对象
     * @param retryPolicy 重试策略
     * @param <T> 结果类型
     * @return 第一次同步执行，重试异步执行
     */
    public static <T> FirstSyncFailSafe with(RetryPolicy retryPolicy) {
        return new FirstSyncFailSafe<T>(retryPolicy);
    }
}
