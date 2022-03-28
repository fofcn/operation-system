package com.github.futurefs.netty.processor;

import com.github.futurefs.netty.netty.NetworkCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author errorfatal89@gmail.com
 */
public interface NettyRequestProcessor extends RequestProcessor {

    NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request)
            throws Exception;

}
