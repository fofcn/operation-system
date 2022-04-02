package com.github.futurefs.store.rpc.config;

import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.config.NettyServerConfig;
import com.github.futurefs.store.rpc.RpcEnum;
import lombok.Builder;
import lombok.Data;

/**
 * Rpc配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01
 */
@Data
@Builder
public class RpcConfig {

    private RpcEnum rpcFramework;

    private RpcClientConfig rpcClientConfig;

    private RpcServerConfig rpcServerConfig;

    public NettyClientConfig toNettyClientConfig() {
        return rpcClientConfig.toNettyClientConfig();
    }

    public NettyServerConfig toNettyServerConfig() {
        return rpcServerConfig.toNettyServerConfig();
    }
}
