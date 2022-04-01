package com.github.futurefs.store.distributed;

import com.github.futurefs.netty.R;
import com.github.futurefs.netty.Service;

/**
 * 集群管理
 *
 * 流程
 * 1、获取配置中的集群方式
 * 2、
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 13:08
 */
public interface ClusterManager extends Service {

    /**
     * 同步偏移
     * @param offset 偏移
     * @return 结果
     */
    R<ClusterResult> syncOffset(long offset);

    /**
     * 增加对等端
     * @param peer 对等端ip  “127.0.0.1::6000”
     */
    void addPeer(String peer);
}
