package com.github.futurefs.netty.network;

/**
 * 请求编码
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 17:14
 */
public class RequestCode {
    /**
     * 文件上传
     */
    public static final int FILE_UPLOAD = 1;

    /**
     * 偏移查询
     */
    public static final int OFFSET_QUERY = 2;

    /**
     * ping
     */
    public static final int PING = 3;

    /**
     * 复制单个文件数据
     */
    public static final int REPLICATE = 4;

    /**
     * 复制请求
     */
    public static final int REPLICATE_DATA = 5;
}
