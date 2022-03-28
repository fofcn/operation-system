package com.github.futurefs.store.index.pubsub;

import com.github.futurefs.store.block.pubsub.BlockFileMessage;
import com.github.futurefs.store.index.IndexTable;
import com.github.futurefs.store.index.IndexNode;
import com.github.futurefs.store.pubsub.Consumer;
import com.github.futurefs.store.pubsub.Message;

/**
 * 块文件消息消费者
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 17:09
 */
public class BlockFileConsumer implements Consumer {

    private final IndexTable indexFile;

    public BlockFileConsumer(IndexTable indexFile) {
        this.indexFile = indexFile;
    }

    @Override
    public void consume(Message message) {
        BlockFileMessage blockFileMsg = (BlockFileMessage) message;
        IndexNode indexNode = new IndexNode();
        indexNode.setDeleteStatus(blockFileMsg.getDeleteStatus());
        indexNode.setKey(blockFileMsg.getKey());
        indexNode.setOffset(blockFileMsg.getOffset());
        indexNode.setSize(blockFileMsg.getSize());

        indexFile.append(indexNode);
    }
}
