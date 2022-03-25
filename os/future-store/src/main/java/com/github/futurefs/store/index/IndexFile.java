package com.github.futurefs.store.index;

import com.github.futurefs.store.common.BaseFile;
import com.github.futurefs.store.common.constant.StoreConstant;
import com.github.futurefs.store.index.pubsub.BlockFileConsumer;
import com.github.futurefs.store.pubsub.Broker;

import java.io.File;
import java.io.IOException;

/**
 * 索引文件
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:31:00
 */
public class IndexFile extends BaseFile {

    private final Broker broker;

    public IndexFile(File file, final Broker broker) {
        super(file);
        this.broker = broker;
    }

    public void append(IndexMeta indexMeta) {
        // todo 实现
    }

    @Override
    protected void doInitNewFile() throws IOException {
        // 文件新建，先预分配超级块
        padFile(4096);
        // 写入超级块魔数
        writeLong(StoreConstant.INDEX_SUPER_MAGIC_NUMBER);
        writeLong(StoreConstant.STORE_VERSION);
        writeLong(0L);
        // 重定位写入位置
        resetWritePos(4096);
    }

    @Override
    protected void doRecover() throws IOException {

    }

    @Override
    protected void doAfterInit() {
        broker.registerConsumer(StoreConstant.BLOCK_TOPIC_NAME, new BlockFileConsumer(this));
    }

}
