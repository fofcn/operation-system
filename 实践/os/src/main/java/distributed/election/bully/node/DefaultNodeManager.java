package distributed.election.bully.node;

import distributed.election.bully.command.CoordinatorRequestHeader;
import distributed.election.bully.command.HeartBeatRequestHeader;
import distributed.election.bully.command.election.ElectionRequestHeader;
import distributed.election.bully.command.election.ElectionResponseHeader;
import distributed.election.bully.config.BullyConfig;
import distributed.election.bully.config.BullyNodeConfig;
import distributed.election.bully.state.RoleEnum;
import distributed.network.NetworkClient;
import distributed.network.enums.RequestCode;
import distributed.network.exception.NetworkConnectException;
import distributed.network.exception.NetworkSendRequestException;
import distributed.network.exception.NetworkTimeoutException;
import distributed.network.netty.NetworkCommand;
import distributed.network.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 默认节点管理
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
@Slf4j
public class DefaultNodeManager implements NodeManager {

    /**
     * 配置文件
     */
    private final BullyConfig bullyConfig;

    /**
     * 网络客户端
     */
    private final NetworkClient networkClient;

    /**
     * 节点配置列表
     */
    private final List<BullyNodeConfig> nodeConfigList = new ArrayList<>(5);

    /**
     * 角色，初始化为非参与者
     */
    private volatile RoleEnum role = RoleEnum.NON_PARTICIPANT;

    /**
     * 节点是自己
     */
    private volatile BullyNodeConfig self;

    /**
     * 协调者节点
     */
    private volatile BullyNodeConfig coordinator;

    /**
     * 选举标识符大于自己的节点列表
     */
    private volatile List<BullyNodeConfig> greaterThanSelfList = new ArrayList<>(5);

    /**
     * 组播线程池
     */
    private final ThreadPoolExecutor broadcastPoolExecutor = new ThreadPoolExecutor(4, 4,
            60L * 100, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.AbortPolicy());

    public DefaultNodeManager(BullyConfig bullyConfig, NetworkClient networkClient) {
        this.bullyConfig = bullyConfig;
        this.networkClient = networkClient;
    }

    @Override
    public boolean initialize() {
        // 解析配置文件
        String clusterNodes = bullyConfig.getClusterNodes();
        if (StringUtil.isEmpty(clusterNodes)) {
            log.error("集群配置不正确，请检查是否符合格式：单节点：标识符+冒号 + 是否为自己(1：自己，0：不是自己) + 冒号 + IP+英文冒号+端口，");
            return false;
        }

        String[] nodesArray = clusterNodes.split(";");
        for (int i = 0; i < nodesArray.length; i++) {
            if (StringUtil.isEmpty(nodesArray[i])) {
                log.error("集群配置不正确，请检查是否符合格式：单节点：标识符+冒号 + 是否为自己(1：自己，0：不是自己) + 冒号 + IP+英文冒号+端口，");
                return false;
            }

            String[] nodeSections = nodesArray[i].split(":");
            if (nodeSections.length != 4) {
                log.error("集群配置不正确，请检查是否符合格式：单节点：标识符+冒号 + 是否为自己(1：自己，0：不是自己) + 冒号 + IP+英文冒号+端口，");
                return false;
            }

            BullyNodeConfig nodeConfig = new BullyNodeConfig();
            nodeConfig.setIdentifier(Integer.parseInt(nodeSections[0]));
            nodeConfig.setSelf(nodeSections[1].equals(0) ? false : true);
            nodeConfig.setIp(nodeSections[2]);
            nodeConfig.setPort(Integer.parseInt(nodeSections[3]));
            nodeConfig.setAddress(nodeSections[2] + ':' + nodeSections[3]);
            nodeConfigList.add(nodeConfig);

            if (nodeSections[1].equals("1")) {
                self = nodeConfig;
            }
        }

        nodeConfigList.addAll(nodeConfigList.stream().sorted(Comparator.comparingInt(BullyNodeConfig::getIdentifier)).collect(Collectors.toList()));
        greaterThanSelfList = nodeConfigList.stream().filter(config -> config.getIdentifier() > self.getIdentifier()).collect(Collectors.toList());

        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public int getRole() {
        return role.getCode();
    }

    @Override
    public int getNodeSize() {
        return nodeConfigList.size();
    }

    @Override
    public boolean victory(int identifier) {
        doVictory(identifier);
        return true;
    }

    @Override
    public void broadcastElection() {
        log.info("start election: identifier: {}, address: {}", self.getIdentifier(), self.getAddress());
        // 直接获胜
        if (directVictory()) {
            doVictory(self.getIdentifier());
        } else {
            // 组播选举消息
            doElectionBroadcast();
        }
    }

    @Override
    public boolean sendHeartBeat() {
        HeartBeatRequestHeader reqHdr = new HeartBeatRequestHeader();
        reqHdr.setIdentifier(self.getIdentifier());
        NetworkCommand req = NetworkCommand.createRequestCommand(RequestCode.HEART_BEAT.getCode(), reqHdr);
        try {
            NetworkCommand response = networkClient.sendSync(coordinator.getAddress(), req, 30L * 1000);
            return NetworkCommand.isResponseOK(response);
        } catch (InterruptedException e) {
            log.error("", e);
        } catch (NetworkTimeoutException e) {
            log.error("", e);
        } catch (NetworkSendRequestException e) {
            log.error("", e);
        } catch (NetworkConnectException e) {
            log.error("", e);
        }

        return false;
    }

    @Override
    public int getIdentifier() {
        return self.getIdentifier();
    }

    @Override
    public void coordinatorVictory(int identifier) {
        Optional<BullyNodeConfig> node = nodeConfigList.stream().filter(e -> e.getIdentifier() == identifier).findFirst();
        if (node.isPresent()) {
            coordinator = node.get();
        }
        role = RoleEnum.FOLLOWER;
    }

    @Override
    public int getCoordinatorId() {
        return coordinator == null ? 0xFFFFFFFF : coordinator.getIdentifier();
    }

    private boolean directVictory() {
        boolean result = nodeConfigList.size() == 1 || greaterThanSelfList.size() == 0;
        Optional<BullyNodeConfig> max = nodeConfigList.stream().max(Comparator.comparingInt(BullyNodeConfig::getIdentifier));
        return result || max.get().getIdentifier() == self.getIdentifier();
    }

    private void doVictory(int identifier) {
        log.info("I'm coordinator, I'm {} and my address is {}", identifier, self.getAddress());
        if (identifier == self.getIdentifier()) {
            role = RoleEnum.COORDINATOR;
            coordinator = self;
        } else {
            Optional<BullyNodeConfig> node = nodeConfigList.stream().filter(e -> e.getIdentifier() == identifier).findFirst();
            if (node.isPresent()) {
                coordinator = node.get();
            }
            role = RoleEnum.FOLLOWER;
        }

        doVictoryBroadcast(identifier);
    }

    private void doVictoryBroadcast(int excludeId) {
        log.info("[Election victory]I will tell you, I'm {} and my address is {}", self.getIdentifier(), self.getAddress());
        // 组播所有的客户端协调者消息
        CountDownLatch countDownLatch = new CountDownLatch(nodeConfigList.size() - 1);
        // 并发送协调者消息
        nodeConfigList.stream().forEach(node -> {
            // 组播不发送给自己
            if (node.getIdentifier() == excludeId) {
                return;
            }

            broadcastPoolExecutor.execute(() -> {
                CoordinatorRequestHeader header = new CoordinatorRequestHeader();
                header.setIdentifier(self.getIdentifier());
                NetworkCommand coordinatorRequest = NetworkCommand.createRequestCommand(RequestCode.COORDINATOR.getCode(), header);
                try {
                    networkClient.sendOneway(node.getAddress(), coordinatorRequest, 30L * 1000, null);
                } catch (InterruptedException e) {
                    log.error("", e);
                } catch (NetworkTimeoutException e) {
                    log.error("", e);
                } catch (NetworkSendRequestException e) {
                    log.error("", e);
                } catch (NetworkConnectException e) {
                    log.error("", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
    }

    private void doElectionBroadcast() {
        role = RoleEnum.PARTICIPANT;

        // 选择比自己大的节点组播选举消息
        CountDownLatch countDownLatch = new CountDownLatch(greaterThanSelfList.size());
        List<Future<NetworkCommand>> futureList = new ArrayList<>(greaterThanSelfList.size());
        greaterThanSelfList.stream().forEach(node -> {
            Future<NetworkCommand> future = broadcastPoolExecutor.submit(() -> {
                ElectionRequestHeader electionReqHdr = new ElectionRequestHeader();
                electionReqHdr.setIdentifier(self.getIdentifier());
                electionReqHdr.setElectionReason(1);
                NetworkCommand electionReq = NetworkCommand.createRequestCommand(RequestCode.ELECTION.getCode(), electionReqHdr);
                try {
                    return networkClient.sendSync(node.getAddress(), electionReq, 30L * 1000);
                } catch (InterruptedException e) {
                    log.error("", e);
                } catch (NetworkTimeoutException e) {
                    log.error("", e);
                } catch (NetworkSendRequestException e) {
                    log.error("", e);
                } catch (NetworkConnectException e) {
                    log.error("", e);
                } finally {
                    countDownLatch.countDown();
                }

                return null;
            });
            futureList.add(future);
        });
        // 等待有节点返回
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("", e);
        }

        // 没有节点返回就直接获胜，组播协调者消息
        int failCnt = 0;
        try {
            for (Future<NetworkCommand> future : futureList) {
                NetworkCommand electResp = future.get();
                if (electResp == null || !NetworkCommand.isResponseOK(electResp)) {
                    failCnt++;
                } else {
                    ElectionResponseHeader respHdr = electResp.decodeHeader(ElectionResponseHeader.class);
                    // 暂时没用
                    // 收到响应就知道有比自己选举标识符大的节点，就静静等协调者消息
                }
            }
        } catch (InterruptedException e) {
            log.error("", e);
        } catch (ExecutionException e) {
            log.error("", e);
        }

        if (failCnt == futureList.size()) {
            // 自己获胜
            log.info("给所有大于我的节点都发了消息，但是你们都没回复我，所以我现在是老大了。");
            doVictoryBroadcast(self.getIdentifier());
        }
    }
}
