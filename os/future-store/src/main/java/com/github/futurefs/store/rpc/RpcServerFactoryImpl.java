package com.github.futurefs.store.rpc;

import com.github.futurefs.store.rpc.config.RpcConfig;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Rpc服务工厂
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:51
 */
public class RpcServerFactoryImpl implements RpcServerFactory {

    @Override
    public RpcServer getRpcServer(RpcConfig rpcConfig) {
        RpcServer rpcServer;
        if (RpcEnum.GRPC.equals(rpcConfig.getRpcFramework())) {
            // todo 待实现
            throw new NotImplementedException();
        } else {
            rpcServer = new NettyRpcServer(rpcConfig);
        }

        return rpcServer;
    }
}
