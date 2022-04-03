package com.github.futurefs.store.distributed.masterslave.processor;

import com.github.futurefs.netty.ClusterProtos;
import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.enums.ResponseCode;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.distributed.masterslave.longpoll.LongPolling;
import com.github.futurefs.store.rpc.RpcClient;
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

    private final RpcClient rpcClient;

    private final int peerId;

    private final LongPolling longPolling;

    public ReplicateProcessor(BlockFile blockFile, RpcClient rpcClient,
                              int peerId, final LongPolling longPolling) {
        this.blockFile = blockFile;
        this.rpcClient = rpcClient;
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
            // 放到长轮询中，直接返回null对象
            longPolling.poll(ctx, req.getOffset());
            return null;
        } else {
            log.error("slave offset errors");
        }

        ByteBuffer buffer = blockFile.read(req.getOffset(), (int) length);
        // 组装请求
        ClusterProtos.ReplicateDataRequest dataRequest =
                ClusterProtos.ReplicateDataRequest.newBuilder()
                .setOffset(req.getOffset())
                .setPeerId(peerId)
                .setSyncLength((int) length)
                .setContent(ByteString.copyFrom(buffer.array()))
                .build();
        ClusterProtos.ClusterRequest clusterRequest = ClusterProtos.ClusterRequest.newBuilder()
                .setDataRequest(dataRequest)
                .build();
        NettyProtos.NettyRequest replicateData = NettyProtos.NettyRequest.newBuilder()
                .setClusterRequest(clusterRequest)
                .build();
//        rpcClient.callOneWay(RequestCode.REPLICATE_DATA, replicateData);

        return NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), replicateData.toByteArray());
    }
}
