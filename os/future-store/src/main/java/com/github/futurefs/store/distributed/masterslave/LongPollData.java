package com.github.futurefs.store.distributed.masterslave;

import lombok.Data;

/**
 * 长轮询数据
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/04 23:06
 */
@Data
public class LongPollData {
    /**
     * 写入偏移
     */
    private long offset;

    /**
     * 文件大小
     */
    private int size;

    /**
     * 文件名
     */
    private long fileKey;

    /**
     * 文件内容
     */
    private byte[] content;

    public LongPollData(long offset, long fileKey, byte[] content) {
        this.offset = offset;
        this.fileKey = fileKey;
        this.content = content;
    }
}
