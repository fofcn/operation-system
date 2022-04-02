package com.github.futurefs.store.distributed.masterslave.processor;

import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.store.block.BlockFile;
import io.netty.channel.ChannelHandlerContext;

/**
 * 复制数据
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 17:00
 */
public class ReplicateDataProcessor implements NettyRequestProcessor {

    private final BlockFile blockFile;

    public ReplicateDataProcessor(BlockFile blockFile) {
        this.blockFile = blockFile;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        return null;
    }
}
