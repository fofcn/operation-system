package distributed.election.bully.processor;

import distributed.election.bully.BullyElectionAlgorithm;
import distributed.election.bully.command.HeartBeatRequestHeader;
import distributed.election.bully.command.HeartBeatResponseHeader;
import distributed.network.enums.ResponseCode;
import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳消息
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
@Slf4j
public class HeartBeatProcessor implements NettyRequestProcessor {

    private final BullyElectionAlgorithm bullyElectionAlgorithm;

    public HeartBeatProcessor(BullyElectionAlgorithm bullyElectionAlgorithm) {
        this.bullyElectionAlgorithm = bullyElectionAlgorithm;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        HeartBeatRequestHeader reqHdr = request.decodeHeader(HeartBeatRequestHeader.class);
        log.info("追随者来信，选举标识符： {}", reqHdr.getIdentifier());

        HeartBeatResponseHeader respHdr = new HeartBeatResponseHeader();
        respHdr.setIdentifier(bullyElectionAlgorithm.getNodeManager().getIdentifier());
        return NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), respHdr);
    }
}
