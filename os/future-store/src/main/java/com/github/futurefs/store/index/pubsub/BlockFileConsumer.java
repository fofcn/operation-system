package com.github.futurefs.store.index.pubsub;

import com.github.futurefs.store.block.pubsub.BlockFileMessage;
import com.github.futurefs.store.index.IndexFile;
import com.github.futurefs.store.index.IndexMeta;
import com.github.futurefs.store.pubsub.Consumer;
import com.github.futurefs.store.pubsub.Message;

/**
 * 块文件消息消费者
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 17:09
 */
public class BlockFileConsumer implements Consumer {

    private final IndexFile indexFile;

    public BlockFileConsumer(IndexFile indexFile) {
        this.indexFile = indexFile;
    }

    @Override
    public void consume(Message message) {
        BlockFileMessage blockFileMsg = (BlockFileMessage) message;
        IndexMeta indexMeta = new IndexMeta();
        indexMeta.setDeleteStatus(blockFileMsg.getDeleteStatus());
        indexMeta.setKey(blockFileMsg.getKey());
        indexMeta.setOffset(blockFileMsg.getOffset());
        indexMeta.setSize(blockFileMsg.getSize());

        indexFile.append(indexMeta);
    }
}
