package com.github.futurefs.store.config;

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
     * 存储文件路径
     */
    private String dir;

    /**
     * 索引文件存储路径
     */
    private String indexDir;

    /**
     * TCP配置
     */
    private NettyServerConfig serverConfig;
}
