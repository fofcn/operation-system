package com.github.futurefs.store.index;

import com.github.futurefs.store.common.Codec;
import com.github.futurefs.store.common.constant.StoreConstant;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 索引节点超级块
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 11:45
 */
@Data
public class IndexSuperBlock implements Codec<IndexSuperBlock> {

    public static final int LENGTH = 3 * Long.BYTES;

    /**
     * 魔数
     */
    private volatile long magic = StoreConstant.INDEX_SUPER_MAGIC_NUMBER;

    /**
     * 文件数量
     */
    private volatile AtomicLong amount = new AtomicLong(0L);

    /**
     * 写入偏移
     */
    private volatile AtomicLong writePos = new AtomicLong(0L);

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
        buffer.putLong(magic);
        buffer.putLong(amount.get());
        buffer.putLong(writePos.get());
        buffer.flip();
        return buffer;
    }

    @Override
    public IndexSuperBlock decode(ByteBuffer buffer) {
        buffer.flip();
        magic = buffer.getLong();
        amount.set(buffer.getLong());
        writePos.set(buffer.getLong());
        return this;
    }
}
