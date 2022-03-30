package com.github.futurefs.store.distributed.raft;

import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import com.alipay.sofa.jraft.storage.impl.RocksDBLogStorage;
import com.alipay.sofa.jraft.util.StorageOptionsFactory;
import com.github.futurefs.netty.OffsetProtos;
import com.github.futurefs.store.distributed.raft.rpc.StoreGrpcHelper;
import com.github.futurefs.store.distributed.raft.rpc.WriteOffsetRequestProcessor;
import org.apache.commons.io.FileUtils;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.IndexType;
import org.rocksdb.util.SizeUnit;

import java.io.File;
import java.io.IOException;

/**
 * 存储Raft Server
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 16:18
 */
public class StoreRaftServer {

    private RaftGroupService raftGroupService;

    private Node node;

    private StoreRaftStateMachine fsm;


    public StoreRaftServer(String dataPath,
                           final String groupId,
                           final PeerId serverId,
                           final NodeOptions nodeOptions) throws IOException {
        FileUtils.forceMkdir(new File(dataPath));

        RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
        StoreGrpcHelper.initGRpc();
        StoreGrpcHelper.setRpcServer(rpcServer);

        rpcServer.registerProcessor(new WriteOffsetRequestProcessor());

        this.fsm = new StoreRaftStateMachine();
        nodeOptions.setFsm(this.fsm);
        nodeOptions.setLogUri(dataPath + File.separator + "log");

        // meta, must
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        // snapshot, optional, generally recommended
        nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot");
        // init raft group service framework
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer);
        BlockBasedTableConfig config = new BlockBasedTableConfig()
                .setIndexType(IndexType.kHashSearch)
                .setBlockSize(16 * SizeUnit.KB)
                .setFilter(new BloomFilter(8, false))
                .setCacheIndexAndFilterBlocks(true)
                .setPinL0FilterAndIndexBlocksInCache(true);
        StorageOptionsFactory.registerRocksDBTableFormatConfig(RocksDBLogStorage.class,config);

        // start raft node
        this.node = this.raftGroupService.start();
    }

    public StoreRaftStateMachine getFsm() {
        return this.fsm;
    }

    public Node getNode() {
        return this.node;
    }

    public RaftGroupService RaftGroupService() {
        return this.raftGroupService;
    }

    /**
     * Redirect request to new leader
     */
    public OffsetProtos.WriteOffsetReply redirect() {
        final OffsetProtos.WriteOffsetReply.Builder builder = OffsetProtos.WriteOffsetReply.newBuilder().setSuccess(false);
        if (this.node != null) {
            final PeerId leader = this.node.getLeaderId();
            if (leader != null) {
//                builder.setRedirect(leader.toString());
            }
        }
        return builder.build();
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 4) {
            System.out
                    .println("Usage : java com.alipay.sofa.jraft.example.counter.CounterServer {dataPath} {groupId} {serverId} {initConf}");
            System.out
                    .println("Example: java com.alipay.sofa.jraft.example.counter.CounterServer /tmp/server1 counter 127.0.0.1:8081 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
            System.exit(1);
        }
        final String dataPath = args[0];
        final String groupId = args[1];
        final String serverIdStr = args[2];
        final String initConfStr = args[3];

        final NodeOptions nodeOptions = new NodeOptions();
        // for test, modify some params
        // set election timeout to 1s
        nodeOptions.setElectionTimeoutMs(1000);
        // disable CLI service。
        nodeOptions.setDisableCli(false);
        // do snapshot every 30s
        nodeOptions.setSnapshotIntervalSecs(30);
        // parse server address
        final PeerId serverId = new PeerId();
        if (!serverId.parse(serverIdStr)) {
            throw new IllegalArgumentException("Fail to parse serverId:" + serverIdStr);
        }
        final Configuration initConf = new Configuration();
        if (!initConf.parse(initConfStr)) {
            throw new IllegalArgumentException("Fail to parse initConf:" + initConfStr);
        }
        // set cluster configuration
        nodeOptions.setInitialConf(initConf);

        // start raft server
        final StoreRaftServer counterServer = new StoreRaftServer(dataPath, groupId, serverId, nodeOptions);
        System.out.println("Started counter server at port:"
                + counterServer.getNode().getNodeId().getPeerId().getPort());
        // GrpcServer need block to prevent process exit
        StoreGrpcHelper.blockUntilShutdown();
    }
}
