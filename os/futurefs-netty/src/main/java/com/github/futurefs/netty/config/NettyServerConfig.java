package com.github.futurefs.netty.config;

import lombok.Data;

/**
 * @author errorfatal89@gmail.com
 */
@Data
public class NettyServerConfig {

    private String host;

    private int listenPort = 8010;

    private int serverWorkerThreads = 8;

    private int serverSelectorThreads = 3;

    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSize = 65535;

    private int serverSocketRcvBufSize = 65535;

    private boolean useTLS = false;

    private String tlsFile;

    @Override
    public String toString() {
        return "NettyServerConfig{" +
                "host='" + host + '\'' +
                ", listenPort=" + listenPort +
                ", serverWorkerThreads=" + serverWorkerThreads +
                ", serverSelectorThreads=" + serverSelectorThreads +
                ", serverChannelMaxIdleTimeSeconds=" + serverChannelMaxIdleTimeSeconds +
                ", serverSocketSndBufSize=" + serverSocketSndBufSize +
                ", serverSocketRcvBufSize=" + serverSocketRcvBufSize +
                ", useTLS=" + useTLS +
                ", tlsFile='" + tlsFile + '\'' +
                '}';
    }
}
