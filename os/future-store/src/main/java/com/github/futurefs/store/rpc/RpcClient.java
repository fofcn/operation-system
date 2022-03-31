package com.github.futurefs.store.rpc;

import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.NetworkClient;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.netty.NettyNetworkClient;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.thread.PoolHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Rpc客户端
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 14:33
 */
@Slf4j
public class RpcClient {

    private final NetworkClient networkClient;

    private final ThreadPoolExecutor broadcastPool;

    private final long waitMillis;

    /**
     * 对等端表
     */
    private final ConcurrentHashMap<Integer, String> peerTable = new ConcurrentHashMap<>(3);

    public RpcClient(final NettyClientConfig nettyClientConfig, final int concurrent, final List<String> peers) {
        this.networkClient = new NettyNetworkClient(nettyClientConfig);
        this.networkClient.start();
        this.waitMillis = nettyClientConfig.getConnectTimeoutMillis();
        this.broadcastPool = PoolHelper.newFixedPool("offset", "offset-", concurrent, 1024);

        for (int i = 0; i < peers.size(); i++) {
            peerTable.put(i, peers.get(i));
        }
    }

    /**
     * 同步调用
     * @param requestCode 请求码
     * @param request 请求
     * @return 结果
     */
    public List<NettyProtos.NettyReply> callSync(int requestCode, NettyProtos.NettyRequest request) {
        NetworkCommand networkCommand = NetworkCommand.createRequestCommand(requestCode, request.toByteArray());
        List<Callable<NettyProtos.NettyReply>> callableList = new ArrayList<>(peerTable.size());
        peerTable.values().forEach(address -> {
            callableList.add(() -> {
                NetworkCommand response = networkClient.sendSync(address, networkCommand, waitMillis);
                if (NetworkCommand.isResponseOk(response)) {
                    return NettyProtos.NettyReply.parseFrom(response.getBody());
                }

                return null;
            });
        });

        return parallelTask(callableList, waitMillis);
    }

    /**
     * 同步组播
     * @param tasks 组播任务
     * @param <T> 组播返回
     * @return
     */
    public <T> List<T> parallelTask(List<Callable<T>> tasks, long waitMillis) {
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }

        List<T> result = new ArrayList<>(tasks.size());
        List<Future<T>> taskResultLst = new ArrayList<>(tasks.size());
        CountDownLatch countDownLatch = new CountDownLatch(tasks.size());
        tasks.forEach(task -> {
            Future<T> taskResult = broadcastPool.submit(task);
            taskResultLst.add(taskResult);
            countDownLatch.countDown();
        });

        // 根据超时时间进行
        try {
            countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("", e);
        }

        // 获取任务执行结果，能获取一个是一个
        for (Future<T> taskFuture : taskResultLst) {
            try {
                T ret = taskFuture.get();
                result.add(ret);
            } catch (ExecutionException | InterruptedException e) {
                log.error("", e);
            }
        }

        return result;
    }

    public void shutdown() {
        networkClient.shutdown();
    }
}
