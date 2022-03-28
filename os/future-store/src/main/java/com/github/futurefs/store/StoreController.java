package com.github.futurefs.store;

import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.config.StoreConfig;
import com.github.futurefs.store.index.IndexNode;
import com.github.futurefs.store.index.IndexTable;
import com.github.futurefs.store.network.StoreNetworkServer;
import com.github.futurefs.store.pubsub.Broker;
import com.github.futurefs.store.pubsub.DefaultBroker;

import java.io.File;

/**
 * 存储控制器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 17:29
 */
public class StoreController {

    private final StoreNetworkServer networkServer;

    private final BlockFile blockFile;

    private final IndexTable indexTable;

    private final StoreConfig storeConfig;

    private final Broker broker;

    public StoreController(StoreConfig storeConfig) {
        this.storeConfig = storeConfig;
        this.broker = new DefaultBroker();
        this.blockFile = new BlockFile(new File(storeConfig.getDir() + File.separator + "block"), broker);
        this.indexTable = new IndexTable(new File(storeConfig.getIndexDir() + File.separator + "index"), broker);
        this.networkServer = new StoreNetworkServer(blockFile, storeConfig.getServerConfig());
    }

    public void init() {
        blockFile.init();
        indexTable.init();
        networkServer.init();
    }

    public void start() {
        networkServer.start();
    }

    public void shutdown() {
        networkServer.shutdown();
        blockFile.close();
        indexTable.close();
    }
}
