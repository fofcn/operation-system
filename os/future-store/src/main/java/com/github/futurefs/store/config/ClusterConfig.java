package com.github.futurefs.store.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.futurefs.store.rpc.config.RpcConfig;
import lombok.Data;

import java.util.List;

/**
 * 集群配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 14:17
 */
@Data
public class ClusterConfig {

    /**
     * 集群模式
     * LEADER-FOLLOWER 目前只支持这个模式
     * LEADER-FOLLOWER-LEARNER
     * LEADER-FOLLOWER-CANDIDATE
     */
    @JsonProperty
    private int mode;

    /**
     * 对等端Id
     */
    @JsonProperty
    private int peerId;

    /**
     * 角色
     */
    @JsonProperty
    private int role;

    /**
     * 地址
     */
    @JsonProperty
    private String address;

    /**
     * 其他节点地址
     */
    @JsonProperty(required = false)
    private List<String> peers;

    @JsonProperty(required = false)
    private RpcConfig rpcConfig;
}
