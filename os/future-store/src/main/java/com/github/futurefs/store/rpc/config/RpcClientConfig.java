package com.github.futurefs.store.rpc.config;

import com.github.futurefs.netty.config.NettyClientConfig;
import lombok.Data;

/**
 * Rpc客户端配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 11:24
 */
@Data
public class RpcClientConfig {

    private int clientWorkerThreads = 4;

    private int connectTimeoutMillis = 30000;

    private long channelNotActiveInterval = 1000L * 60;

    private int clientChannelMaxIdleTimeSeconds = 120;

    private int clientSocketSndBufSize = 65535;

    private int clientSocketRcvBufSize = 65535;

    private int queueCapacity = 1000;

    private boolean useTLS = false;

    private String tlsFile;

    public NettyClientConfig toNettyClientConfig() {
        // todo 拷贝属性
        return new NettyClientConfig();
    }
}
