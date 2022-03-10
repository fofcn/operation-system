package distributed.election.bully.fault;

import distributed.election.bully.config.BullyConfig;
import distributed.election.bully.node.DefaultNodeManager;
import distributed.election.bully.node.NodeManager;
import distributed.election.bully.state.RoleEnum;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 默认故障检测器
 *
 * @author errorfatal90@gmail.com
 * @date 2022/03/10
 */
public class DefaultFaultDetector implements FaultDetector {

    /**
     * 节点管理器
     */
    private final NodeManager nodeManager;

    /**
     * 协调者故障检测定时器
     */
    private final ScheduledThreadPoolExecutor coordinatorDetectorTimer =
            new ScheduledThreadPoolExecutor(1,
                    r -> new Thread(r, "coordinator-detector"),
                    new ThreadPoolExecutor.AbortPolicy());

    public DefaultFaultDetector(BullyConfig bullyConfig, NodeManager nodeManager) {
        if (nodeManager == null) {
            nodeManager = new DefaultNodeManager(bullyConfig);
        }
        this.nodeManager = nodeManager;
    }

    @Override
    public void start() {
        // 初始启动检查自己是否为
        coordinatorDetectorTimer.scheduleAtFixedRate(() -> {
            // 检查自己的角色状态
            // 如果是非参与者，那么启动选举
            if (RoleEnum.NON_PARTICIPANT.getCode() == nodeManager.getRole()) {
                // 立即启动选举算法
                // 发送选举消息
                nodeManager.broadcastElection();
            } else if (RoleEnum.COORDINATOR.getCode() == nodeManager.getRole()) {
                // 什么也不做
            } else if (RoleEnum.FOLLOWER.getCode() == nodeManager.getRole()) {
                // 发送心跳消息，你还好吗，老大？
            } else {
                // 什么也不做
            }


        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        coordinatorDetectorTimer.shutdown();
    }
}
