package com.github.futurefs.client.api.impl;

import com.github.futurefs.client.api.ApiResult;
import com.github.futurefs.client.api.ApiResultWrapper;
import com.github.futurefs.client.api.TrickyClient;
import com.github.futurefs.client.api.rpc.RpcClient;
import com.github.futurefs.netty.FileDataProtos.FileRequest;
import com.github.futurefs.netty.NettyProtos.NettyReply;
import com.github.futurefs.netty.NettyProtos.NettyRequest;
import com.github.futurefs.netty.exception.TrickyFsNetworkException;
import com.github.futurefs.netty.network.RequestCode;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.InputStream;

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
    public ApiResult write(String bucket, byte[] content) throws TrickyFsNetworkException {
        long fileKey = writeFile(content);
        return fileKey == -1L ? ApiResultWrapper.fail("") :
                ApiResultWrapper.success(fileKey);
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

    private long writeFile(byte[] content) throws TrickyFsNetworkException {
        NettyRequest request;
        FileRequest fileRequest = FileRequest.newBuilder()
                .setData(ByteString.copyFrom(content))
                .setLength(content.length)
                .build();
        request = NettyRequest.newBuilder().setFileRequest(fileRequest).build();
        NettyReply reply = rpcClient.callSync(RequestCode.FILE_UPLOAD, request);
        if (reply.getFileReply().getSuccess()) {
            return reply.getFileReply().getKey();
        }

        return -1L;
    }
}
