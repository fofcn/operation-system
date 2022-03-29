package com.github.futurefs.netty.thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池辅助
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:42
 */
public class PoolHelper {

    private static final ConcurrentHashMap<String, AtomicInteger> moduleThreadIdxTable = new ConcurrentHashMap<>(12);

    /**
     * 新建定时器线程池
     * @param module 模块名称
     * @param threadName 线程名
     * @param coreSize 线程池大小
     * @return 定时器线程池
     */
    public static ScheduledThreadPoolExecutor newScheduledExecutor(String module, String threadName, int coreSize) {
        AtomicInteger idx = moduleThreadIdxTable.get(module);
        if (idx == null) {
            idx = new AtomicInteger(0);
        }

        AtomicInteger finalIdx = idx;
        return new ScheduledThreadPoolExecutor(coreSize, r -> new Thread(r, threadName + "-" + finalIdx.get()), new ThreadPoolExecutor.AbortPolicy());
    }
}
