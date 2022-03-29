package com.github.futurefs.client.api.impl;

import com.github.futurefs.client.api.ApiResult;
import com.github.futurefs.client.api.ClientApi;

import java.io.File;
import java.io.InputStream;

/**
 * 客户端Api实现
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:13
 */
public class ClientApiImpl implements ClientApi {

    @Override
    public ApiResult write(String bucket, byte[] content) {
        // 流程
        // 1. 获取bucket
        // 2. 获取bucket下可写存储节点列表
        // 3. 多播写入文件内容到存储节点下
        // 4. 获取结果返回
        return null;
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
}
