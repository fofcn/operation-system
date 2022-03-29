package com.github.futurefs.store.config;

import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.config.NettyServerConfig;
import lombok.Data;

import java.util.List;

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
    private List<String> buckets;

    /**
     * 存储文件路径
     */
    private String dir;

    /**
     * 索引文件存储路径
     */
    private String indexDir;

    /**
     * bucket集群配置
     */
    private List<String> bucketCluster;

    /**
     * TCP server配置
     */
    private NettyServerConfig serverConfig;

    /**
     * TCP client配置
     */
    private NettyClientConfig clientConfig;

    /**
     * raft协议使用的数据目录
     */
    private String raftDataPath;
}
