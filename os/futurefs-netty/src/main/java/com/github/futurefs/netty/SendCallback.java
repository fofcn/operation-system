package com.github.futurefs.netty;


import com.github.futurefs.netty.netty.ResponseFuture;

/**
 * one way和异步消息回调
 *
 * @author errorfatal89@gmail.com
 */
public interface SendCallback {

    /**
     * 操作完成后回调
     * @param responseFuture 响应控制
     */
    void operationComplete(ResponseFuture responseFuture);
}
