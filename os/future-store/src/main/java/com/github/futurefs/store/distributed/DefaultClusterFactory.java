package com.github.futurefs.store.distributed;

import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.config.ClusterConfig;
import com.github.futurefs.store.distributed.masterslave.MasterSlaveClusterImpl;
import com.github.futurefs.store.pubsub.Broker;

/**
 * 集群工厂实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/05 23:06
 */
public class DefaultClusterFactory implements ClusterFactory {

    @Override
    public ClusterManager getCluster(ClusterConfig clusterConfig, Broker broker, BlockFile blockFile) {
        ClusterMode clusterMode = ClusterMode.getByCode(clusterConfig.getMode());
        if (clusterMode.equals(ClusterMode.INVALID_CLUSTER)) {
            return null;
        }

        if (ClusterMode.MASTER_SLAVE.equals(clusterMode)) {
            return new MasterSlaveClusterImpl(clusterConfig, broker, blockFile);
        } else {
            throw new UnsupportedOperationException("not support mode " + clusterMode);
        }
    }
}
