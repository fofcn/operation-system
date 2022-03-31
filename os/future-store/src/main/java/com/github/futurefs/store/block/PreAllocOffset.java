package com.github.futurefs.store.block;

import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.OffsetProtos.WriteOffsetRequest;
import com.github.futurefs.netty.NettyProtos.NettyRequest;
import com.github.futurefs.netty.NettyProtos.NettyReply;
import com.github.futurefs.netty.network.RequestCode;
import com.github.futurefs.store.rpc.RpcClient;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 预分配偏移
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/30 16:21
 */
public class PreAllocOffset {

    /**
     * 预分配偏移，只保留在内存中不写入到磁盘，系统启动时从BlockFile的超级块writePos字段中获取初始值
     */
    private AtomicLong offset = new AtomicLong(0L);

    private final RpcClient rpcClient;

    public PreAllocOffset(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public void set(long pos) {
        offset.set(pos);
    }

    public long alloc(int length) {
        long newOffset = offset.getAndAdd(length);

        // todo 防止其他节点不能写入偏移，先预分配一个偏移，然后询问其他节点是否能够满足分配

        // todo 这里需要校验磁盘空间是否满足

        WriteOffsetRequest offsetRequest = WriteOffsetRequest.newBuilder().setLength(length).build();
        NettyRequest request = NettyRequest.newBuilder().setOffsetRequest(offsetRequest).build();
        List<NettyReply> replyList = rpcClient.callSync(RequestCode.OFFSET_QUERY, request);
        if (CollectionUtils.isEmpty(replyList)) {
            return -1L;
        }

        replyList = replyList.stream().filter(reply -> reply != null && reply.getOffsetReply().getSuccess())
                .collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(replyList) ? newOffset : -1L;
    }
}
