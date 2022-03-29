package com.github.futurefs.netty;

/**
 * 服务基础接口
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:21
 */
public interface Service {

    /**
     * 初始化
     * @return
     */
    boolean init();

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void shutdown();
}
