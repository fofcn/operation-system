package com.github.futurefs.store.block;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 超级块
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:30:00
 */
@Data
public class SuperBlock {
    /**
     * 魔数
     */
    private volatile long magic;

    /**
     * 版本号
     */
    private volatile long version;

    /**
     * 文件数量
     */
    private volatile AtomicLong amount = new AtomicLong(0L);

    /**
     * 写入偏移
     */
    private volatile AtomicLong writePos = new AtomicLong(0L);

    public SuperBlock(long magic, long version, long amount, long writePos) {
        this.magic = magic;
        this.version = version;
        this.amount.set(amount);
        this.writePos.set(writePos);
    }

    public ByteBuffer encode() {
        int length = 4 * Long.BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.putLong(magic);
        buffer.putLong(version);
        buffer.putLong(amount.get());
        buffer.putLong(writePos.get());
        buffer.flip();
        return buffer;
    }
}
