package distributed.election.bully.fault;

import distributed.election.bully.BullyElectionAlgorithm;
import distributed.election.bully.node.NodeManager;
import distributed.election.bully.state.RoleEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认故障检测器
 *
 * @author errorfatal90@gmail.com
 * @date 2022/03/10
 */
@Slf4j
public class DefaultFaultDetector implements FaultDetector {

    /**
     * 故障计数阈值，达到这个次数就发起选举
     */
    private static final int FAULT_THRESHOLD = 3;

    /**
     * 节点管理器
     */
    private final NodeManager nodeManager;

    /**
     * 超时计数器
     */
    private final AtomicInteger faultCounter = new AtomicInteger(0);

    /**
     * 协调者故障检测定时器
     */
    private final ScheduledThreadPoolExecutor coordinatorDetectorTimer =
            new ScheduledThreadPoolExecutor(1,
                    r -> new Thread(r, "coordinator-detector"),
                    new ThreadPoolExecutor.AbortPolicy());

    public DefaultFaultDetector(BullyElectionAlgorithm bullyElectionAlgorithm) {
        this.nodeManager = bullyElectionAlgorithm.getNodeManager();
    }

    @Override
    public void start() {
        // 初始启动检查自己是否为
        coordinatorDetectorTimer.scheduleAtFixedRate(() -> {
            log.info("当前协调者标识符： {}， 当前角色： {}， 当前选举标识符: {}",
                    nodeManager.getCoordinatorId(), nodeManager.getRole(), nodeManager.getIdentifier());
            // 检查自己的角色状态
            // 如果是非参与者，那么启动选举
            if (RoleEnum.NON_PARTICIPANT.getCode() == nodeManager.getRole()
                    || RoleEnum.PARTICIPANT.getCode() == nodeManager.getRole()) {
                // 立即启动选举算法
                // 发送选举消息
                nodeManager.broadcastElection();
            } else if (RoleEnum.FOLLOWER.getCode() == nodeManager.getRole()) {
                // 发送心跳消息，你还好吗，老大？
                boolean result = nodeManager.sendHeartBeat();
                if (!result) {
                    int curCnt = faultCounter.incrementAndGet();
                    if (curCnt > FAULT_THRESHOLD) {
                        log.warn("Coordinator fault, start election.");
                        nodeManager.broadcastElection();
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        coordinatorDetectorTimer.shutdown();
    }
}
