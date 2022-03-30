package com.github.futurefs.store.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.config.NettyServerConfig;
import lombok.Data;


/**
 * 存储配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/24 13:50
 */
@Data
public class StoreConfig {
    /**
     * 角色： LEADER/FOLLOWER
     */
    @JsonProperty
    private String role;

    /**
     * bucket列表
     */
    @JsonProperty
    private String buckets;

    /**
     * 存储文件路径
     */
    @JsonProperty
    private String blockPath;

    /**
     * 索引文件存储路径
     */
    @JsonProperty
    private String indexPath;

    /**
     * bucket集群配置
     */
    @JsonProperty
    private String bucketCluster;

    /**
     * TCP server配置
     */
    @JsonProperty
    private NettyServerConfig serverConfig;

    /**
     * TCP client配置
     */
    @JsonProperty
    private NettyClientConfig clientConfig;

    /**
     * raft协议使用的数据目录
     */
    @JsonProperty
    private String raftDataPath;

}
