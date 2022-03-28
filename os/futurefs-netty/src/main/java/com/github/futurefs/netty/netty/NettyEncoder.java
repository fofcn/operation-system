
package com.github.futurefs.netty.netty;

import com.github.futurefs.netty.util.NetworkUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author errorfatal89@gmail.com
 */
public class NettyEncoder  extends MessageToByteEncoder<NetworkCommand>  {

    private static final Logger log = LoggerFactory.getLogger("Network");

    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkCommand networkCommand, ByteBuf byteBuf) throws Exception {
        try {
            ByteBuffer byteBuffer = networkCommand.encode();
            byteBuf.writeBytes(byteBuffer.array());
        } catch (Throwable e) {
            log.error("encode exception, " + NetworkUtil.parseChannelRemoteAddr(ctx.channel()), e);
            NetworkUtil.closeChannel(ctx.channel());
        }
    }
}
