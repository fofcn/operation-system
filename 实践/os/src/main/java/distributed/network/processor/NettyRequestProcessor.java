package distributed.network.processor;

import distributed.network.netty.NetworkCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author errorfatal89@gmail.com
 */
public interface NettyRequestProcessor extends RequestProcessor {

    NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request)
            throws Exception;

}
