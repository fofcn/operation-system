package com.github.futurefs.store.distributed.masterslave;

import com.github.futurefs.netty.ClusterProtos;
import com.github.futurefs.netty.EnumUtil;
import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.NettyProtos.NettyReply;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.exception.TrickyFsException;
import com.github.futurefs.netty.network.RequestCode;
import com.github.futurefs.netty.thread.PoolHelper;
import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.common.constant.StoreConstant;
import com.github.futurefs.store.distributed.ClusterConfig;
import com.github.futurefs.store.distributed.ClusterManager;
import com.github.futurefs.store.distributed.ClusterMode;
import com.github.futurefs.store.distributed.masterslave.longpoll.LongPolling;
import com.github.futurefs.store.distributed.masterslave.processor.PingProcessor;
import com.github.futurefs.store.distributed.masterslave.processor.ReplicateProcessor;
import com.github.futurefs.store.pubsub.Broker;
import com.github.futurefs.store.rpc.RpcClient;
import com.github.futurefs.store.rpc.RpcServer;
import com.github.futurefs.store.rpc.RpcServerFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主从集群模式实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:07
 */
@Slf4j
public class MasterSlaveClusterImpl implements ClusterManager {

    private final ScheduledThreadPoolExecutor timerExecutor = PoolHelper.newScheduledExecutor("masterSlave", "master-slave-", 1);

    private final ClusterConfig clusterConfig;

    private final RpcClient rpcClient;

    private final RpcServer rpcServer;

    private final ClusterMode clusterMode;

    private volatile MasterSlaveRole masterSlaveRole;

    private final Broker broker;

    private final BlockFile blockFile;

    private final ConcurrentHashMap<Integer, String> peerTable = new ConcurrentHashMap<>(4);

    private final AtomicInteger timeoutCounter = new AtomicInteger(0);

    private final LongPolling<LongPollData, LongPollArgs> longPolling;

    public MasterSlaveClusterImpl(final ClusterConfig clusterConfig, final NettyClientConfig nettyClientConfig, final Broker broker, final BlockFile blockFile) {
        this.clusterConfig = clusterConfig;
        this.broker = broker;
        this.blockFile = blockFile;
        this.rpcServer = new RpcServerFactoryImpl().getRpcServer(clusterConfig.getRpcConfig());
        ClusterMode confMode = ClusterMode.getByCode(clusterConfig.getClusterMode());
        if (confMode.equals(ClusterMode.INVALID_CLUSTER)) {
            throw new IllegalArgumentException("invalid cluster configuration: " + clusterConfig.getClusterMode());
        }
        this.clusterMode = confMode;

        MasterSlaveRole confRole = EnumUtil.getByCode(MasterSlaveRole.values(), clusterConfig.getRole());
        if (confMode.equals(MasterSlaveRole.INVALID)) {
            throw new IllegalArgumentException("invalid cluster configuration: " + clusterConfig.getRole());
        }
        masterSlaveRole = confRole;

        parsePeer();

        this.rpcClient = new RpcClient(nettyClientConfig, 1, new ArrayList<>(peerTable.values()));

        this.longPolling = new LongPolling();
    }

    @Override
    public void addPeer(String peer) {
    }

    @Override
    public int getPeerId() {
        return clusterConfig.getPeerId();
    }


    @Override
    public boolean init() {
        rpcServer.init();
        rpcServer.registerProcessor(RequestCode.PING, new PingProcessor(this, blockFile));
        rpcServer.registerProcessor(RequestCode.REPLICATE, new ReplicateProcessor(blockFile, clusterConfig.getPeerId(),
                longPolling));

        // 文件新增消费者，监听文件新增以满足长轮询
        broker.registerConsumer(StoreConstant.BLOCK_TOPIC_NAME, new ClusterConsumer(blockFile, longPolling));


        // 启动ping定时任务
        timerExecutor.scheduleAtFixedRate(() -> {
            if (masterSlaveRole.equals(MasterSlaveRole.SLAVE)) {
                // 组装请求
                ClusterProtos.PingRequest pingReq = ClusterProtos.PingRequest.newBuilder()
                        .setPeerId(clusterConfig.getPeerId())
                        .setTotalSpace(0)
                        .setUsedSpace(0)
                        .setFreeSpace(0)
                        .setCanWrite(true)
                        .build();
                ClusterProtos.ClusterRequest clusterRequest = ClusterProtos.ClusterRequest.newBuilder()
                        .setPingRequest(pingReq).build();
                NettyProtos.NettyRequest request = NettyProtos.NettyRequest.newBuilder()
                        .setClusterRequest(clusterRequest)
                        .build();
                List<NettyReply> replyList = rpcClient.callSync(RequestCode.PING, request);
                replyList.forEach(reply -> {
                    ClusterProtos.PingReply pingReply = reply.getClusterReply().getPingReply();
                    if (!pingReply.getSuccess()) {
                        log.error("master error");
                    }
                });
            }
        }, 0L, 500L, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void start() {
        rpcServer.start();
    }

    @Override
    public void shutdown() {
        rpcServer.shutdown();
    }

    /**
     * 解析对等配置
     */
    private void parsePeer() {
        List<String> peerList = clusterConfig.getPeerList();
        peerList.forEach(peer -> {
            if (StringUtils.isNotEmpty(peer)) {
                String[] peerParts = peer.split(":");
                if (peerParts.length != 3) {
                    log.error("peer config error, config:<{}>", peer);
                    return;
                }

                peerTable.put(Integer.parseInt(peerParts[0]), peerParts[1] + ":" + peerParts[2]);
            }
        });
    }

    /**
     * 发送同步请求
     * @param masterWritePos
     * @param writePos
     */
    private void sendSyncRequest(long masterWritePos, long writePos) {
        // 计算master 写入和自身的写入偏移差异
        if (masterWritePos - writePos > 0) {
            // 偏移大于0就开始同步
            ClusterProtos.ReplicateRequest replicateRequest = ClusterProtos.ReplicateRequest.newBuilder()
                    .setOffset(writePos)
                    .setPeerId(clusterConfig.getPeerId())
                    .build();
            ClusterProtos.ClusterRequest clusterRequest = ClusterProtos.ClusterRequest.newBuilder()
                    .setReplicateRequest(replicateRequest)
                    .build();
            NettyProtos.NettyRequest request = NettyProtos.NettyRequest.newBuilder()
                    .setClusterRequest(clusterRequest)
                    .build();

            try {
                rpcClient.callOneWay(RequestCode.REPLICATE, request);
            } catch (TrickyFsException e) {
                e.printStackTrace();
            }
        }

    }
}
