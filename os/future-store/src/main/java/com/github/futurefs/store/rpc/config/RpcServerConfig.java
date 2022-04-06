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
        NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setHost(host);
        serverConfig.setListenPort(listenPort);
        serverConfig.setServerWorkerThreads(serverWorkerThreads);
        serverConfig.setServerSelectorThreads(serverSelectorThreads);
        serverConfig.setServerChannelMaxIdleTimeSeconds(serverChannelMaxIdleTimeSeconds);
        serverConfig.setServerSocketSndBufSize(serverSocketSndBufSize);
        serverConfig.setServerSocketRcvBufSize(serverSocketRcvBufSize);
        serverConfig.setUseTLS(useTLS);
        serverConfig.setTlsFile(tlsFile);
        return serverConfig;
    }
}
