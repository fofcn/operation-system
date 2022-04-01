package com.github.futurefs.store.rpc;

import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.config.NettyServerConfig;
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


    public NettyClientConfig toNettyClientConfig() {

    }

    public NettyServerConfig toNettyServerConfig() {

    }
}
