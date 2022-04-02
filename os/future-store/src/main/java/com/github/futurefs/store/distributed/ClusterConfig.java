package com.github.futurefs.store.distributed;

import com.github.futurefs.store.rpc.config.RpcConfig;
import lombok.Data;

import java.util.List;

/**
 * 集群配置类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 16:57
 */
@Data
public class ClusterConfig {

    /**
     * 集群模式
     */
    private int clusterMode;

    /**
     * 集群角色
     */
    private int role;

    /**
     * 对等端ID
     */
    private int peerId;

    /**
     * Rpc配置
     */
    private RpcConfig rpcConfig;

    /**
     *
     */
    private List<String> peerList;
}
