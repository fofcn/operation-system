package com.github.futurefs.store.distributed;

/**
 * 集群工厂类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 ：53
 */
public interface ClusterFactory {

    /**
     * 获取集群管理器
     *
     * @param clusterConfig 集群配置
     * @return 集群管理器
     */
    ClusterManager getCluster(ClusterConfig clusterConfig);
}
