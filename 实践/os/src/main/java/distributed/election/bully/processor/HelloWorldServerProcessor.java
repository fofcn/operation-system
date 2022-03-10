package distributed.election.bully.processor;

import distributed.election.bully.command.HelloWorldRequestHeader;
import distributed.election.bully.command.HelloWorldResponseHeader;
import distributed.network.NetworkServer;
import distributed.network.enums.RequestCode;
import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import distributed.network.util.NetworkUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * Hello world
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/09
 */
public class HelloWorldServerProcessor implements NettyRequestProcessor {
    private final NetworkServer networkServer;

    public HelloWorldServerProcessor(final NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        HelloWorldRequestHeader header = request.decodeHeader(HelloWorldRequestHeader.class);
        System.out.println("from " + NetworkUtil.parseChannelRemoteAddr(ctx.channel()) + ": " + header.getEcho());

        HelloWorldResponseHeader responseHdr = new HelloWorldResponseHeader();
        responseHdr.setEcho("Hi, Client: Hello World!");
        NetworkCommand response = NetworkCommand.createRequestCommand(RequestCode.ELECTION_ACK.getCode(), responseHdr);
        networkServer.sendOneway(ctx.channel(), response, 30L * 1000, null);
        return null;
    }
}
