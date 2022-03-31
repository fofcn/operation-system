package com.github.futurefs.client.api.impl;

import com.github.futurefs.client.api.ApiResult;
import com.github.futurefs.client.api.ApiResultWrapper;
import com.github.futurefs.client.api.TrickyClient;
import com.github.futurefs.client.api.rpc.RpcClient;
import com.github.futurefs.netty.FileDataProtos;
import com.github.futurefs.netty.FileDataProtos.FileRequest;
import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.OffsetProtos;
import com.github.futurefs.netty.OffsetProtos.WriteOffsetRequest;
import com.github.futurefs.netty.NettyProtos.NettyRequest;
import com.github.futurefs.netty.NettyProtos.NettyReply;
import com.github.futurefs.netty.network.RequestCode;
import com.google.protobuf.ByteString;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户端Api实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:13
 */
public class TrickyClientImpl implements TrickyClient {

    private final RpcClient rpcClient;

    public TrickyClientImpl(final RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public ApiResult write(String bucket, byte[] content) {
        long writeOffset = getWriteOffset(content.length);
        long fileKey = writeFile(content, writeOffset);
        return ApiResultWrapper.success(fileKey);
    }

    @Override
    public ApiResult write(String bucket, File file) {
        return null;
    }

    @Override
    public ApiResult write(String bucket, String filePath) {
        return null;
    }

    @Override
    public ApiResult write(String bucket, InputStream inputFile) {
        return null;
    }

    @Override
    public ApiResult read(String bucket, long fileId) {
        return null;
    }

    private long writeFile(byte[] content, long writeOffset) {
        NettyRequest request;
        List<NettyReply> replyList;// 组装文件写入返回
        FileRequest fileRequest = FileRequest.newBuilder()
                .setData(ByteString.copyFrom(content))
                .setLength(content.length)
                .setOffset(writeOffset)
                .build();
        request = NettyRequest.newBuilder().setFileRequest(fileRequest).build();
        replyList = rpcClient.callSync(RequestCode.FILE_UPLOAD, request);
        if (CollectionUtils.isNotEmpty(replyList)) {
            replyList = replyList.stream().filter(reply -> reply != null && reply.getOffsetReply().getSuccess()).collect(Collectors.toList());
        }

        NettyReply fileReply = replyList.get(0);
        return fileReply.getFileReply().getKey();
    }

    private long getWriteOffset(int length) {
        // 获取偏移
        WriteOffsetRequest offsetRequest = WriteOffsetRequest.newBuilder().setLength(length).build();
        NettyRequest request = NettyRequest.newBuilder().setOffsetRequest(offsetRequest).build();
        List<NettyReply> replyList = rpcClient.callSync(RequestCode.OFFSET_QUERY, request);
        if (CollectionUtils.isNotEmpty(replyList)) {
            replyList = replyList.stream().filter(reply -> reply != null && reply.getOffsetReply().getSuccess()).collect(Collectors.toList());
        }
        NettyReply offsetReply = replyList.get(0);
        return offsetReply.getOffsetReply().getOffset();
    }
}
