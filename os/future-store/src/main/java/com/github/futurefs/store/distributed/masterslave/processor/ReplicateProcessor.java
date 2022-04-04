package com.github.futurefs.store.distributed.masterslave.processor;

import com.github.futurefs.netty.ClusterProtos;
import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.enums.ResponseCode;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.distributed.masterslave.LongPollArgs;
import com.github.futurefs.store.distributed.masterslave.MasterSlaveLongPollCallback;
import com.github.futurefs.store.distributed.masterslave.longpoll.LongPollCallback;
import com.github.futurefs.store.distributed.masterslave.longpoll.LongPolling;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 复制处理器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 15:03
 */
@Slf4j
public class ReplicateProcessor implements NettyRequestProcessor {

    private final BlockFile blockFile;

    private final int peerId;

    private final LongPolling longPolling;

    public ReplicateProcessor(final BlockFile blockFile,
                              final int peerId,
                              final LongPolling longPolling) {
        this.blockFile = blockFile;
        this.peerId = peerId;
        this.longPolling = longPolling;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        ClusterProtos.ReplicateRequest req = ClusterProtos.ReplicateRequest.parseFrom(request.getBody());

        long diff = blockFile.getWritePos() - req.getOffset();
        // 从blockFile中获取有效数据传输,每次获取1MB
        long length = 1024 * 1024;
        if (diff < 1024 * 1024) {
            length = diff;
        } else if (diff == 0) {
            LongPollArgs lpArgs = new LongPollArgs(ctx, req.getOffset(), blockFile);
            LongPollCallback lpCallback = new MasterSlaveLongPollCallback(peerId);
            longPolling.poll(String.valueOf(req.getPeerId()),
                    lpCallback, lpArgs);
            return null;
        } else {
            log.error("slave offset errors");
        }

        ByteBuffer buffer = blockFile.read(req.getOffset(), (int) length);
        // 组装请求
        ClusterProtos.ReplicateReply dataReply =
                ClusterProtos.ReplicateReply.newBuilder()
                .setExists(true)
                .setOffset(req.getOffset())
                .setPeerId(peerId)
                .setSyncLength((int) length)
                .setContent(ByteString.copyFrom(buffer.array()))
                .build();
        ClusterProtos.ClusterReply clusterReply = ClusterProtos.ClusterReply.newBuilder()
                .setReplicateReply(dataReply)
                .build();
        NettyProtos.NettyReply reply = NettyProtos.NettyReply.newBuilder()
                .setClusterReply(clusterReply)
                .build();

        return NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), reply.toByteArray());
    }
}
