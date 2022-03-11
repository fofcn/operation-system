package distributed.election.bully.processor;

import distributed.election.bully.BullyElectionAlgorithm;
import distributed.election.bully.command.CoordinatorRequestHeader;
import distributed.election.bully.node.NodeManager;
import distributed.election.bully.state.RoleEnum;
import distributed.network.netty.NetworkCommand;
import distributed.network.processor.NettyRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 协调者消息处理器
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
@Slf4j
public class CoordinatorProcessor implements NettyRequestProcessor {

    private final BullyElectionAlgorithm bullyElectionAlgorithm;

    public CoordinatorProcessor(BullyElectionAlgorithm bullyElectionAlgorithm) {
        this.bullyElectionAlgorithm = bullyElectionAlgorithm;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        // 取请求头
        CoordinatorRequestHeader reqHdr = request.decodeHeader(CoordinatorRequestHeader.class);
        log.info("接收到协调者消息， identifier: {}", reqHdr.getIdentifier());
        NodeManager nodeManager = bullyElectionAlgorithm.getNodeManager();
        // 如果自己当前是协调者，那么查看消息中的选举标识符是不是比自己大
        // 如果比自己大，那么就设置别人为协调者并设置自己是追随者
        if (nodeManager.getRole() == RoleEnum.COORDINATOR.getCode()) {
            if (nodeManager.getIdentifier() < reqHdr.getIdentifier()) {
                log.info("新的协调者出现了，我要下台了。");
                nodeManager.victory(reqHdr.getIdentifier());
            } else {
                // todo 协调者比自己小，应该回复个消息：你给我退下
                log.warn("有人在捣乱");
            }
        } else {
            nodeManager.coordinatorVictory(reqHdr.getIdentifier());
        }

        // 返回空，不响应消息
        return null;
    }
}
