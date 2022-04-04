package com.github.futurefs.store.distributed.masterslave.longpoll;

import com.github.futurefs.netty.thread.PoolHelper;
import com.github.futurefs.netty.util.NetworkUtil;
import com.github.futurefs.store.block.BlockFile;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 长轮询
 * 流程
 * 1. 集群消费者监听到文件新增
 * 2. 发送消息到长轮询
 * 3. 长轮询监听到文件新增后检查有没有  没有拉取到复制内容的slave
 * 4. 如果没有则直接返回
 * 5. 如果有则返回文件数据给slave
 * 6. 释放slave的长轮询请求
 * 7. 检测到长轮询的请求长时间没有得到满足主动释放掉长轮询请求并返回无文件更新响应
 *
 * 结构设计
 * 1. slave与slave请求信息表（请求信息包括：请求ID、请求起始偏移）
 * 2. 消息队列：一个或多个文件
 *
 * 方法设计：
 * 1. 添加消息： 消息入队
 *    查找slave请求，如果偏移一致则组装同步响应返回并释放slave长轮询请求
 *    如果不一致释放slave的长轮询请求让slave重新请求
 *
 * 定时器设计
 * 1. 轮询slave请求
 * 2. 查看轮询起始时间
 * 3. 如果轮询起始时间大于两个扫描周期（以时间对比）则释放slave长轮询请求
 * 4. 发送消息给slave：无数据更新，请再次重试
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 23:18
 */
@Slf4j
public class LongPolling<Data, Args> {

    /**
     * 长轮询客户端
     */
    private final ConcurrentHashMap<String, LongPollingClient> clientTable = new ConcurrentHashMap<>(4);

    /**
     * 定时器，定时扫描客户端是否超时，超时则回调客户端回调函数
     */
    private final ScheduledThreadPoolExecutor scanTimer = PoolHelper.newScheduledExecutor("longPolling", "longPolling-", 1);

    /**
     * 增加轮询
     * @param id 客户端上下文
     * @param callback 请求偏移
     * @param args 请求偏移
     */
    public boolean poll(String id, LongPollCallback<Data, Args> callback, Args args) {
        // 添加客户端到长轮询表中
        LongPollingClient longPollingClient = new LongPollingClient(callback, args);
        clientTable.putIfAbsent(id, longPollingClient);
        return true;
    }

    public void notifyWrite(Data data) {
        Iterator<Map.Entry<String, LongPollingClient>> iterator = clientTable.entrySet().iterator();
        while (iterator.hasNext()) {
            LongPollingClient client = iterator.next().getValue();
            client.getCallback().onNewData(data, client.getArgs());
        }
    }

    public void scan() {
        // 扫描所有过期的请求
        scanTimer.scheduleAtFixedRate(() -> {
            if (clientTable.isEmpty()) {
                return;
            }

            Iterator<Map.Entry<String, LongPollingClient>> iterator = clientTable.entrySet().iterator();
            while (iterator.hasNext()) {
                LongPollingClient client = iterator.next().getValue();
                long period = System.currentTimeMillis() - client.getTime();
                if (period >= 30L * 1000 * 2) {
                    client.getCallback().onTimeout(client.getArgs());
                    log.info("client <{}> wait timeout, remove it.", iterator.next().getKey());
                    iterator.remove();
                }
            }

        }, 30L, 30L, TimeUnit.SECONDS);
    }
}
