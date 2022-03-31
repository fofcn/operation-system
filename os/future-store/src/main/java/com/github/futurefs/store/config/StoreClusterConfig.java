package com.github.futurefs.store.config;

import lombok.Data;

import java.util.List;

/**
 * 集群配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 14:17
 */
@Data
public class StoreClusterConfig {

    /**
     * 集群模式
     * LEADER-FOLLOWER 目前只支持这个模式
     * LEADER-FOLLOWER-LEARNER
     * LEADER-FOLLOWER-CANDIDATE
     */
    private String mode;

    /**
     * 对等端Id
     */
    private int peerId;

    /**
     * 角色
     */
    private String role;

    /**
     * 地址
     */
    private String address;

    /**
     * 其他节点地址
     */
    private List<String> nodes;
}
