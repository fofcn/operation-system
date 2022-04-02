package com.github.futurefs.store.rpc.config;

import com.github.futurefs.netty.config.NettyServerConfig;
import lombok.Data;

/**
 * Rpc服务端配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 14:14
 */
@Data
public class RpcServerConfig {

    private String host;

    private int listenPort = 8010;

    private int serverWorkerThreads = 8;

    private int serverSelectorThreads = 3;

    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSize = 65535;

    private int serverSocketRcvBufSize = 65535;

    private boolean useTLS = false;

    private String tlsFile;

    public NettyServerConfig toNettyServerConfig() {
        // todo 拷贝属性
        return new NettyServerConfig();
    }
}
