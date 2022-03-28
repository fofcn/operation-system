package com.github.futurefs.netty.config;

/**
 * @author errorfatal89@gmail.com
 */
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getServerWorkerThreads() {
        return serverWorkerThreads;
    }

    public void setServerWorkerThreads(int serverWorkerThreads) {
        this.serverWorkerThreads = serverWorkerThreads;
    }

    public int getServerSelectorThreads() {
        return serverSelectorThreads;
    }

    public void setServerSelectorThreads(int serverSelectorThreads) {
        this.serverSelectorThreads = serverSelectorThreads;
    }

    public int getServerSocketSndBufSize() {
        return serverSocketSndBufSize;
    }

    public void setServerSocketSndBufSize(int serverSocketSndBufSize) {
        this.serverSocketSndBufSize = serverSocketSndBufSize;
    }

    public int getServerSocketRcvBufSize() {
        return serverSocketRcvBufSize;
    }

    public void setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
        this.serverSocketRcvBufSize = serverSocketRcvBufSize;
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return serverChannelMaxIdleTimeSeconds;
    }

    public void setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
        this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
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
