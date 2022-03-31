package com.github.futurefs.client.api;

import com.github.futurefs.client.api.config.ClientConfig;
import com.github.futurefs.client.api.impl.TrickyClientImpl;
import com.github.futurefs.client.api.rpc.RpcClient;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 客户端管理
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 18:13
 */
public class ClientManager {

    private RpcClient rpcClient;

    private final ClientConfig clientConfig;

    public ClientManager(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public void init() {
        this.rpcClient = new RpcClient(clientConfig.getTcpClientConfig(), clientConfig.getThreadCnt(), clientConfig.getStoreNodes());
    }

    public void shutdown() {
        this.rpcClient.shutdown();
    }

    public TrickyClient getClient() {
        return new TrickyClientImpl(rpcClient);
    }

}
