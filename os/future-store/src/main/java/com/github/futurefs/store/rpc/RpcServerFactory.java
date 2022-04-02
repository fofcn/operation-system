package com.github.futurefs.store.rpc;

import com.github.futurefs.store.rpc.config.RpcConfig;

/**
 * Rpc服务工厂
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:51
 */
public interface RpcServerFactory {

    /**
     * 获取rpc 服务
     * @param rpcConfig rpc配置
     * @return rpc类
     */
    RpcServer getRpcServer(RpcConfig rpcConfig);
}
