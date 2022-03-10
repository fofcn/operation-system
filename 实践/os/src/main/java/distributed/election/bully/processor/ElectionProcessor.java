package distributed.election.bully.processor;

import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;

/**
 * 选举请求处理
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public class ElectionProcessor implements NettyRequestProcessor {

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        return null;
    }
}
