package com.github.futurefs.store.disk;

import com.github.futurefs.netty.NetworkClient;
import com.github.futurefs.netty.Service;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.netty.NettyNetworkClient;
import com.github.futurefs.netty.thread.PoolHelper;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 硬盘管理
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:20
 */
public class DiskManager implements Service {


    public DiskManager() {

    }

    @Override
    public boolean init() {

        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
