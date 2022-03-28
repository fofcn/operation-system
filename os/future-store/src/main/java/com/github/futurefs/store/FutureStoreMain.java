package com.github.futurefs.store;

import com.github.futurefs.netty.config.NettyServerConfig;
import com.github.futurefs.store.config.StoreConfig;

/**
 * 存储主函数
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:27:00
 */
public class FutureStoreMain {

    public static void main(String[] args) {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setDir("storeDir");
        storeConfig.setIndexDir("indexDir");
        NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(60000);
        storeConfig.setServerConfig(serverConfig);
        StoreController controller = new StoreController(storeConfig);
        controller.init();
        controller.start();
    }
}
