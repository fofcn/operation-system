package com.github.futurefs.store.rpc;

import com.github.futurefs.netty.NetworkServer;
import com.github.futurefs.netty.Service;
import com.github.futurefs.netty.config.NettyServerConfig;
import com.github.futurefs.netty.interceptor.RequestInterceptor;
import com.github.futurefs.netty.netty.NettyNetworkServer;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.netty.processor.RequestProcessor;

/**
 * RpcServer端
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:22
 */
public class NettyRpcServer implements RpcServer {

    private final NetworkServer server;

    public NettyRpcServer(final RpcConfig rpcConfig) {
        this.server = new NettyNetworkServer(rpcConfig.toNettyServerConfig());
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void shutdown() {
        server.shutdown();
    }

    @Override
    public void registerProcessor(int code, RequestProcessor processor) {
        server.registerProcessor(code, (NettyRequestProcessor) processor, null);
    }

    @Override
    public void registerInterceptor(RequestInterceptor interceptor) {
        server.registerInterceptor(interceptor);
    }
}
