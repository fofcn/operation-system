package com.github.futurefs.store.raft;

import com.alipay.sofa.jraft.Closure;
import com.github.futurefs.netty.OffsetProtos;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 16:40
 */
public abstract class StoreRaftClosure implements Closure {
    private OffsetProtos.WriteOffsetReply valueResponse;
    private StoreRaftOperation storeRaftOperation;

    public void setCounterOperation(StoreRaftOperation storeRaftOperation) {
        this.storeRaftOperation = storeRaftOperation;
    }

    public StoreRaftOperation getCounterOperation() {
        return storeRaftOperation;
    }

    public OffsetProtos.WriteOffsetReply getValueResponse() {
        return valueResponse;
    }

    public void setValueResponse(OffsetProtos.WriteOffsetReply valueResponse) {
        this.valueResponse = valueResponse;
    }

    protected void failure(final String errorMsg) {
        final OffsetProtos.WriteOffsetReply response = OffsetProtos.WriteOffsetReply.newBuilder().setSuccess(false).setErrMsg(errorMsg)
                .build();
        setValueResponse(response);
    }

    protected void success(final long value) {
        final OffsetProtos.WriteOffsetReply response = OffsetProtos.WriteOffsetReply.newBuilder().setOffset(value).setSuccess(true).build();
        setValueResponse(response);
    }
}
