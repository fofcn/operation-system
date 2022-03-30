package com.github.futurefs.store.distributed.raft;

import java.io.Serializable;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 17:02
 */
public class StoreRaftOperation implements Serializable {

    private volatile long writePos;

    public long getWritePos() {
        return writePos;
    }

    public void setWritePos(long writePos) {
        this.writePos = writePos;
    }
}
