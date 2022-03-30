package com.github.futurefs.store.block;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 预分配偏移
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/30 16:21
 */
public class PreAllocOffset {

    /**
     * 预分配偏移，只保留在内存中不写入到磁盘，系统启动时从BlockFile的超级块writePos字段中获取初始值
     */
    private AtomicLong offset = new AtomicLong(0L);

    public void set(long pos) {
        offset.set(pos);
    }

    public long alloc(int length) {
        return offset.getAndAdd(length);
    }
}
