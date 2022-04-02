package com.github.futurefs.netty.config;

import lombok.Data;

/**
 * @author errorfatal89@gmail.com
 */
@Data
public class NettyClientConfig {

    private int clientWorkerThreads = 4;

    private int connectTimeoutMillis = 30000;

    private long channelNotActiveInterval = 1000L * 60;

    private int clientChannelMaxIdleTimeSeconds = 120;

    private int clientSocketSndBufSize = 65535;

    private int clientSocketRcvBufSize = 65535;

    private int queueCapacity = 1000;

    private boolean useTLS = false;

    private String tlsFile;

    @Override
    public String toString() {
        return "NettyClientConfig{" +
                "clientWorkerThreads=" + clientWorkerThreads +
                ", connectTimeoutMillis=" + connectTimeoutMillis +
                ", channelNotActiveInterval=" + channelNotActiveInterval +
                ", clientChannelMaxIdleTimeSeconds=" + clientChannelMaxIdleTimeSeconds +
                ", clientSocketSndBufSize=" + clientSocketSndBufSize +
                ", clientSocketRcvBufSize=" + clientSocketRcvBufSize +
                ", queueCapacity=" + queueCapacity +
                ", useTLS=" + useTLS +
                ", tlsFile='" + tlsFile + '\'' +
                '}';
    }
}
