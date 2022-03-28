package com.github.futurefs.netty.config;

/**
 * @author errorfatal89@gmail.com
 */
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

    public int getClientWorkerThreads() {
        return clientWorkerThreads;
    }

    public void setClientWorkerThreads(int clientWorkerThreads) {
        this.clientWorkerThreads = clientWorkerThreads;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public long getChannelNotActiveInterval() {
        return channelNotActiveInterval;
    }

    public void setChannelNotActiveInterval(long channelNotActiveInterval) {
        this.channelNotActiveInterval = channelNotActiveInterval;
    }

    public int getClientChannelMaxIdleTimeSeconds() {
        return clientChannelMaxIdleTimeSeconds;
    }

    public void setClientChannelMaxIdleTimeSeconds(int clientChannelMaxIdleTimeSeconds) {
        this.clientChannelMaxIdleTimeSeconds = clientChannelMaxIdleTimeSeconds;
    }

    public int getClientSocketSndBufSize() {
        return clientSocketSndBufSize;
    }

    public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
        this.clientSocketSndBufSize = clientSocketSndBufSize;
    }

    public int getClientSocketRcvBufSize() {
        return clientSocketRcvBufSize;
    }

    public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
        this.clientSocketRcvBufSize = clientSocketRcvBufSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public boolean isUseTLS() {
        return useTLS;
    }

    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }

    public String getTlsFile() {
        return tlsFile;
    }

    public void setTlsFile(String tlsFile) {
        this.tlsFile = tlsFile;
    }

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
