package distributed.network.netty;

import distributed.network.enums.NettyEventType;
import distributed.network.util.NetworkUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author errorfatal89@gmail.com
 */
public class NettyConnectManagerHandler extends ChannelDuplexHandler {

    private static final Logger log = LoggerFactory.getLogger(NettyConnectManagerHandler.class);

    private final NettyNetworkServer nettyNetworkServer;
    public NettyConnectManagerHandler(NettyNetworkServer nettyNetworkServer) {
        this.nettyNetworkServer = nettyNetworkServer;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
        log.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
        log.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
        log.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
        super.channelActive(ctx);

        if (nettyNetworkServer.getChannelEventListener() != null) {
            nettyNetworkServer.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remoteAddress, ctx.channel()));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
        log.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
        super.channelInactive(ctx);

        if (nettyNetworkServer.getChannelEventListener() != null) {
            nettyNetworkServer.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
                log.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
                NetworkUtil.closeChannel(ctx.channel());
                if (nettyNetworkServer.getChannelEventListener() != null) {
                    nettyNetworkServer.putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress, ctx.channel()));
                }
            }
        }

        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = NetworkUtil.parseChannelRemoteAddr(ctx.channel());
        log.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
        log.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

        if (nettyNetworkServer.getChannelEventListener() != null) {
            nettyNetworkServer.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
        }

        NetworkUtil.closeChannel(ctx.channel());
    }
}
