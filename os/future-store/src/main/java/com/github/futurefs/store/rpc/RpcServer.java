package com.github.futurefs.store.rpc;

import com.github.futurefs.netty.Service;
import com.github.futurefs.netty.interceptor.RequestInterceptor;
import com.github.futurefs.netty.processor.RequestProcessor;

/**
 * Rpc 服务接口
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:27
 */
public interface RpcServer extends Service {

    /**
     * 注册消息处理器
     * @param code 消息码
     * @param processor 消息处理器
     */
    void registerProcessor(int code, RequestProcessor processor);

    /**
     * 注册消息拦截器
     * @param interceptor 消息拦截器
     */
    void registerInterceptor(RequestInterceptor interceptor);
}
