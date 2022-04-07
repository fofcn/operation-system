package com.github.futurefs.store.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.config.NettyServerConfig;
import com.github.futurefs.store.common.flush.FlushStrategyConfig;
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
     * 输盘配置
     */
    @JsonProperty
    private FlushStrategyConfig flushConfig;

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
     * 集群配置
     */
    @JsonProperty
    private ClusterConfig clusterConfig;

}
