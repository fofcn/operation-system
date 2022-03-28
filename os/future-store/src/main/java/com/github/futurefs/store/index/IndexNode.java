package com.github.futurefs.store.index;

import com.github.futurefs.store.common.Codec;
import lombok.Data;

import java.nio.ByteBuffer;

/**
 * 索引元数据
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:31:00
 */
@Data
public class IndexNode implements Codec<IndexNode> {

    public static final int LENGTH = 4 * Long.BYTES;

    private long key;

    private long offset;

    private long size;

    private long deleteStatus;

    @Override
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
        buffer.putLong(key);
        buffer.putLong(offset);
        buffer.putLong(size);
        buffer.putLong(deleteStatus);
        buffer.flip();
        return buffer;
    }

    @Override
    public IndexNode decode(ByteBuffer buffer) {
        key = buffer.getLong();
        offset = buffer.getLong();
        size = buffer.getLong();
        deleteStatus = buffer.getLong();
        return this;
    }
}
