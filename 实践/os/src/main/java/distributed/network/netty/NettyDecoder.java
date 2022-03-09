package distributed.network.netty;

import distributed.network.util.NetworkUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


/**
 * @author errorfatal89@gmail.com
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger log = LoggerFactory.getLogger("Network");
    private static final int FRAME_MAX_LENGTH = 16777216;

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf networkFrame = null;
        try {
            networkFrame = (ByteBuf) super.decode(ctx, in);
            if (null == networkFrame) {
                return null;
            }

            ByteBuffer byteBuffer = networkFrame.nioBuffer();
            return NetworkCommand.decode(byteBuffer);
        } catch (Exception e) {
            log.error("decode exception, " + NetworkUtil.parseChannelRemoteAddr(ctx.channel()), e);
            NetworkUtil.closeChannel(ctx.channel());
        } finally {
            if (networkFrame != null) {
                networkFrame.release();
            }
        }

        return null;
    }
}
