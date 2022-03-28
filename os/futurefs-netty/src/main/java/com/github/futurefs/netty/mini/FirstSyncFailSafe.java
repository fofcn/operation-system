package com.github.futurefs.netty.mini;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 第一次同步执行，重试异步执行
 * @param <T> 结果类型
 *
 * @author errorfatal89@gmail.com
 */
public class FirstSyncFailSafe<T> {
    private static final Logger log = LoggerFactory.getLogger("Common");
    private final RetryPolicy retryPolicy;
    private volatile ScheduledExecutorService scheduledExecutorService;
    private volatile AtomicInteger maxRetries;

    public FirstSyncFailSafe(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        this.maxRetries = new AtomicInteger(retryPolicy.getMaxRetries());
    }

    /**
     * 设置定时服务
     * @param scheduledExecutorService 定时服务
     * @return this
     */
    public FirstSyncFailSafe<T> withScheduler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        return this;
    }

    /**
     * 执行并获取同步执行结构
     * @param callable 用户自定义任务
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     */
    public <T> T get(Callable<T> callable) {
        CallableWrapper<T> callableWrapper = new CallableWrapper<T>(callable);
        try {
            return callableWrapper.call();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * callable包装类
     * @param <T> 用户结果类型
     */
    class CallableWrapper<T> implements Callable<T> {
        private final Callable<T> callable;

        CallableWrapper(Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        public T call() throws Exception {
            T returnResult = null;
            Throwable failure = null;
            try {
                //先进行直接调用，调用失败后进行重试操作

                T result = callable.call();
                returnResult = result;

            } catch (Throwable e) {
                failure = e;
            }

            //执行到最后一次
            if (maxRetries.decrementAndGet() <= 0) {
                return null;
            }

            T finalReturnResult = returnResult;
            Throwable finalFailure = failure;

            //开始重试
            if (retryPolicy.canRetryFor(finalReturnResult, finalFailure)) {
                log.warn("enter task retry phase, maxRetries: [{}], task: [{}]", maxRetries, callable);
                scheduledExecutorService.schedule(this,
                        retryPolicy.getDelay(),
                        retryPolicy.getDelayTimeUnit());
            }

            return returnResult;
        }
    }
}


