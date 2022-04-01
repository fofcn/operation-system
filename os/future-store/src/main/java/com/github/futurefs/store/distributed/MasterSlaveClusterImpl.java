package com.github.futurefs.store.distributed;

import com.github.futurefs.netty.R;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.store.rpc.RpcClient;
import com.github.futurefs.store.rpc.RpcServer;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 主从集群模式实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:07
 */
public class MasterSlaveClusterImpl implements ClusterManager {

    private final CopyOnWriteArrayList<String> peers = new CopyOnWriteArrayList<String>(3);

    private final ClusterConfig clusterConfig;

    private final RpcClient rpcClient;

    private final RpcServer rpcServer;

    public MasterSlaveClusterImpl(final ClusterConfig clusterConfig, final NettyClientConfig nettyClientConfig) {
        this.clusterConfig = clusterConfig;
        this.rpcClient = new RpcClient(nettyClientConfig, 4, peers);
    }

    @Override
    public R<ClusterResult> syncOffset(long offset) {

    }

    @Override
    public void addPeer(String peer) {
        peers.add(peer);
    }


    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
