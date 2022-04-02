package com.github.futurefs.store.index;

import com.github.futurefs.netty.R;
import com.github.futurefs.netty.RWrapper;
import com.github.futurefs.store.common.AppendResult;
import com.github.futurefs.store.common.BaseFile;
import com.github.futurefs.store.common.constant.StoreConstant;
import com.github.futurefs.store.index.pubsub.IndexFileConsumer;
import com.github.futurefs.store.pubsub.Broker;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

/**
 * 索引文件
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:31:00
 */
public class IndexTable extends BaseFile {

    private final Broker broker;

    private final IndexSuperBlock superBlock;

    private volatile MappedByteBuffer mappedBuffer;

    public IndexTable(File file, final Broker broker) {
        super(file);
        this.broker = broker;
        this.superBlock = new IndexSuperBlock();
    }

    public void append(IndexNode indexNode) {
        if (ObjectUtils.isEmpty(indexNode)) {
            throw new IllegalArgumentException("");
        }

        ByteBuffer buffer = indexNode.encode();
        R<AppendResult> appendResult = super.append(buffer);
        if (RWrapper.isSuccess(appendResult)) {

        }
    }

    @Override
    protected void doInitNewFile() throws IOException {
        // 文件新建，先预分配超级块
        padFile(StoreConstant.SUPER_BLOCK_LENGTH);
        // 写入超级块魔数
        writeLong(superBlock.getMagic());
        // 写入文件数量
        writeLong(superBlock.getAmount().get());
        // 重定位写入位置
        resetWritePos(StoreConstant.SUPER_BLOCK_LENGTH);

        mappedBuffer = map(0, 4096);
    }

    @Override
    protected void doRecover() throws IOException {
        // 读取超级块
        ByteBuffer buffer = read(0L, IndexSuperBlock.LENGTH);
        superBlock.decode(buffer);
        // todo 核对数据文件数量和索引节点数量，不一致需要从数据文件中恢复索引节点
        // 重置写入偏移
        resetWritePos(superBlock.getAmount().get() * IndexNode.LENGTH);
    }

    @Override
    protected void doAfterInit() {
        broker.registerConsumer(StoreConstant.BLOCK_TOPIC_NAME, new IndexFileConsumer(this));
    }

}
