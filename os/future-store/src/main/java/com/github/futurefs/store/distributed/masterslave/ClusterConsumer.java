//package com.github.futurefs.store.distributed.masterslave;
//
//import com.github.futurefs.netty.ClusterProtos;
//import com.github.futurefs.netty.NettyProtos;
//import com.github.futurefs.netty.network.RequestCode;
//import com.github.futurefs.store.block.BlockFile;
//import com.github.futurefs.store.block.FileBlock;
//import com.github.futurefs.store.block.pubsub.BlockFileMessage;
//import com.github.futurefs.store.pubsub.Consumer;
//import com.github.futurefs.store.pubsub.Message;
//import com.github.futurefs.store.rpc.RpcClient;
//
//import java.util.List;
//
///**
// * 集群消费同步
// *
// * @author errorfatal89@gmail.com
// * @datetime 2022/04/02 15:54
// */
//public class ClusterConsumer implements Consumer {
//
//    private final BlockFile blockFile;
//
//    private final RpcClient rpcClient;
//
//    public ClusterConsumer(BlockFile blockFile,  final RpcClient rpcClient) {
//        this.blockFile = blockFile;
//        this.rpcClient = rpcClient;
//    }
//
//
//    @Override
//    public void consume(Message message) {
//        BlockFileMessage blockFileMsg = (BlockFileMessage) message;
//
//        FileBlock fileBlock = blockFile.read(blockFileMsg.getOffset());
//        // 组装消息进行同步
//        ClusterProtos.ReplicateRequest replicateRequest = ClusterProtos.ReplicateRequest.newBuilder()
//                .setOffset((int) blockFileMsg.getSize())
//                .setOffset(blockFileMsg.getOffset())
//                .build();
//        ClusterProtos.ClusterRequest clusterReq = ClusterProtos.ClusterRequest.newBuilder()
//                .setReplicateRequest(replicateRequest)
//                .build();
//        NettyProtos.NettyRequest request = NettyProtos.NettyRequest.newBuilder()
//                .setClusterRequest(clusterReq)
//                .build();
//        List<NettyProtos.NettyReply> replyList = rpcClient.callSync(RequestCode.REPLICATE, request);
//            if (reply != null) {
//                ClusterProtos.ReplicateReply replicateReply = reply.getClusterReply().getReplicateReply();
//                replicateReply.getSuccess()
//            }
//        });
//    }
//}
