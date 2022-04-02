package com.github.futurefs.store.distributed.masterslave.processor;

import com.github.futurefs.netty.ClusterProtos;
import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.enums.ResponseCode;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.distributed.masterslave.MasterSlaveClusterImpl;
import io.netty.channel.ChannelHandlerContext;

/**
 * ping请求处理
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 17:20
 */
public class PingProcessor implements NettyRequestProcessor {

    private final MasterSlaveClusterImpl masterSlaveCluster;

    private final BlockFile blockFile;

    public PingProcessor(MasterSlaveClusterImpl masterSlaveCluster, final BlockFile blockFile) {
        this.masterSlaveCluster = masterSlaveCluster;
        this.blockFile = blockFile;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        ClusterProtos.PingRequest pingRequest = ClusterProtos.PingRequest.parseFrom(request.getBody());
        masterSlaveCluster.addPeer(pingRequest.getAddress());

        // 组装响应
        ClusterProtos.PingReply reply = ClusterProtos.PingReply.newBuilder()
                .setPeerId(masterSlaveCluster.getPeerId())
                .setWritePos(blockFile.getWritePos())
                .build();
        ClusterProtos.ClusterReply clusterReply = ClusterProtos.ClusterReply.newBuilder()
                .setPingReply(reply)
                .build();
        NettyProtos.NettyReply nettyRequest = NettyProtos.NettyReply.newBuilder()
                .setClusterReply(clusterReply)
                .build();

        return NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), nettyRequest.toByteArray());
    }
}
