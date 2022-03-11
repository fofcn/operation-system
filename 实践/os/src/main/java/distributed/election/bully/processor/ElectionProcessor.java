package distributed.election.bully.processor;

import distributed.election.bully.BullyElectionAlgorithm;
import distributed.election.bully.command.election.ElectionRequestHeader;
import distributed.election.bully.command.election.ElectionResponseHeader;
import distributed.election.bully.node.NodeManager;
import distributed.network.enums.RequestCode;
import distributed.network.enums.ResponseCode;
import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 选举请求处理
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
@Slf4j
public class ElectionProcessor implements NettyRequestProcessor {

    private final BullyElectionAlgorithm bullyElectionAlgorithm;

    /**
     * 异步线程池
     */
    private final ThreadPoolExecutor asyncPoolExecutor = new ThreadPoolExecutor(1, 1,
            60L * 100, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.AbortPolicy());

    public ElectionProcessor(BullyElectionAlgorithm bullyElectionAlgorithm) {
        this.bullyElectionAlgorithm = bullyElectionAlgorithm;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        // 收到选举消息
        ElectionRequestHeader reqHdr = request.decodeHeader(ElectionRequestHeader.class);
        NodeManager nodeManager = bullyElectionAlgorithm.getNodeManager();
        ElectionResponseHeader respHdr = new ElectionResponseHeader();
        NetworkCommand resp = null;
        if (nodeManager.getIdentifier() <= reqHdr.getIdentifier()) {
            log.error("错误");
            resp = NetworkCommand.createResponseCommand(ResponseCode.TRANSACTION_FAILED.getCode(), respHdr);
        } else {
            respHdr.setIdentifier(nodeManager.getIdentifier());
            resp = NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), respHdr);
        }

        // 开始另一次选举
        asyncPoolExecutor.execute(() -> nodeManager.broadcastElection());

        return resp;
    }
}
