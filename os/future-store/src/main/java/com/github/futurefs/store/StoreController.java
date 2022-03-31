package com.github.futurefs.store;

import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.config.StoreConfig;
import com.github.futurefs.store.index.IndexTable;
import com.github.futurefs.store.network.StoreNetworkServer;
import com.github.futurefs.store.pubsub.Broker;
import com.github.futurefs.store.pubsub.DefaultBroker;
import com.github.futurefs.store.rpc.RpcClient;

import java.io.File;

/**
 * 存储控制器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 17:29
 */
public class StoreController {

    private final StoreNetworkServer storeServer;

    private final BlockFile blockFile;

    private final IndexTable indexTable;

    private final Broker broker;

    private final RpcClient rpcClient;

    public StoreController(StoreConfig storeConfig) {
        this.broker = new DefaultBroker();
        this.rpcClient = new RpcClient(storeConfig.getClientConfig(), 4, storeConfig.getClusterConfig().getNodes());
        this.blockFile = new BlockFile(new File(storeConfig.getBlockPath() + File.separator + "block"), broker, rpcClient);
        this.indexTable = new IndexTable(new File(storeConfig.getIndexPath() + File.separator + "index"), broker);
        this.storeServer = new StoreNetworkServer(blockFile, storeConfig.getServerConfig());
    }

    public void init() {
        blockFile.init();
        indexTable.init();
        storeServer.init();
    }

    public void start() {
        storeServer.start();
    }

    public void shutdown() {
        storeServer.shutdown();
        blockFile.close();
        indexTable.close();
        rpcClient.shutdown();
    }
}
