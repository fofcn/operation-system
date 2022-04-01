package com.github.futurefs.store.network;

import com.github.futurefs.netty.NetworkServer;
import com.github.futurefs.netty.config.NettyServerConfig;
import com.github.futurefs.netty.netty.NettyNetworkServer;
import com.github.futurefs.netty.network.RequestCode;
import com.github.futurefs.store.block.BlockFile;

/**
 * 存储TCP服务
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 17:10
 */
public class StoreNetworkServer {

    private NetworkServer server;

    private final BlockFile blockFile;

    private final NettyServerConfig nettyServerConfig;

    public StoreNetworkServer(final BlockFile blockFile, final NettyServerConfig nettyServerConfig) {
        this.blockFile = blockFile;
        this.nettyServerConfig = nettyServerConfig;
    }

    public boolean init() {
        server = new NettyNetworkServer(nettyServerConfig);
        server.registerProcessor(RequestCode.FILE_UPLOAD, new FileDataProcessor(blockFile), null);
        server.registerProcessor(RequestCode.OFFSET_QUERY, new FileOffsetProcessor(blockFile.getPreAllocOffset()), null);
        return true;
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }
}
