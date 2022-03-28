package com.github.futurefs.netty.interceptor;


import com.github.futurefs.netty.netty.NetworkCommand;

/**
 * 请求拦截器
 * @author errorfatal89@gmail.com
 */
public interface RequestInterceptor {

    /**
     * 处理请求前处理
     * @param remoteAddr 远程地址
     * @param request 请求命令
     * @return true:拦截器处理成功，请求继续处理；false:拦截器处理失败，请求处理中断
     * @throws Exception
     */
    boolean doBeforeRequest(final String remoteAddr, final NetworkCommand request) throws Exception;

    /**
     * 处理完请求后处理
     * @param remoteAddr
     * @param request
     * @param response
     * @throws Exception
     */
    void doAfterResponse(final String remoteAddr, final NetworkCommand request,
                         final NetworkCommand response) throws Exception;
}
