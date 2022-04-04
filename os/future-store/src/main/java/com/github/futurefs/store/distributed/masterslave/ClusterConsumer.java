package com.github.futurefs.store.distributed.masterslave;

import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.block.FileBlock;
import com.github.futurefs.store.block.pubsub.BlockFileMessage;
import com.github.futurefs.store.distributed.masterslave.longpoll.LongPolling;
import com.github.futurefs.store.pubsub.Consumer;
import com.github.futurefs.store.pubsub.Message;
import com.github.futurefs.store.rpc.RpcClient;

/**
 * 集群消费同步
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 15:54
 */
public class ClusterConsumer implements Consumer {

    private final BlockFile blockFile;

    private final LongPolling longPolling;

    public ClusterConsumer(final BlockFile blockFile,
                           final LongPolling longPolling) {
        this.blockFile = blockFile;
        this.longPolling = longPolling;
    }


    @Override
    public void consume(Message message) {
        BlockFileMessage blockFileMsg = (BlockFileMessage) message;

        FileBlock fileBlock = blockFile.read(blockFileMsg.getOffset());

        LongPollData longPollData = new LongPollData(blockFileMsg.getOffset(),
                fileBlock.getHeader().getKey(),
                fileBlock.getBody());
        longPolling.notifyWrite(longPollData);
    }
}
