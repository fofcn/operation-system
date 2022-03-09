package distributed.election.bully.processor;

import distributed.election.bully.command.HelloWorldRequestHeader;
import distributed.election.bully.command.HelloWorldResponseHeader;
import distributed.network.enums.RequestCode;
import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import distributed.network.util.NetworkUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/09
 */
public class HelloWorldClientProcessor implements NettyRequestProcessor {
    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        HelloWorldResponseHeader header = request.decodeHeader(HelloWorldResponseHeader.class);
        System.out.println("from " + NetworkUtil.parseChannelRemoteAddr(ctx.channel()) + ": " + header.getEcho());
        return null;
    }
}
