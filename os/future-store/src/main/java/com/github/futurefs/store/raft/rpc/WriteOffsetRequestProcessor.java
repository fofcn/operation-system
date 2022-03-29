package com.github.futurefs.store.raft.rpc;

import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import com.github.futurefs.netty.OffsetProtos;

/**
 * 写入偏移请求处理器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 16:38
 */
public class WriteOffsetRequestProcessor implements RpcProcessor<OffsetProtos.WriteOffsetRequest> {
    @Override
    public void handleRequest(RpcContext rpcContext, OffsetProtos.WriteOffsetRequest writeOffsetRequest) {

    }

    @Override
    public String interest() {
        return OffsetProtos.WriteOffsetRequest.class.getName();
    }
}
