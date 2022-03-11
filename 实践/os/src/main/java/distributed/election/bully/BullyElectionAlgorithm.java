package distributed.election.bully;

import distributed.election.bully.command.HelloWorldRequestHeader;
import distributed.election.bully.config.BullyConfig;
import distributed.election.bully.fault.DefaultFaultDetector;
import distributed.election.bully.fault.FaultDetector;
import distributed.election.bully.node.DefaultNodeManager;
import distributed.election.bully.node.NodeManager;
import distributed.election.bully.processor.CoordinatorProcessor;
import distributed.election.bully.processor.ElectionProcessor;
import distributed.election.bully.processor.HeartBeatProcessor;
import distributed.election.bully.processor.HelloWorldClientProcessor;
import distributed.election.bully.processor.HelloWorldServerProcessor;
import distributed.network.NetworkClient;
import distributed.network.NetworkServer;
import distributed.network.config.NettyClientConfig;
import distributed.network.config.NettyServerConfig;
import distributed.network.enums.RequestCode;
import distributed.network.exception.NetworkConnectException;
import distributed.network.exception.NetworkSendRequestException;
import distributed.network.exception.NetworkTimeoutException;
import distributed.network.netty.NettyNetworkClient;
import distributed.network.netty.NettyNetworkServer;
import distributed.network.netty.NetworkCommand;

/**
 * 霸道选举算法
 * 流程节点
 * 1. 配置加载与解析
 * 2. 发起选举
 *  2.1 if 集群中只有自己一个节点，
 *          then 直接选择自己为Coordinator;
 *  2.2 else
 *          if 如果自己的选举标识符比所有的都大，
 *              then 直接向所有的节点发送协调者消息；
 *          else
 *              向所有大于自己选举标识符的节点发送选举消息，等待响应；
 *              if 等待超时后没有节点回复，
 *                  then 直接选举自己为协调者并向所有的节点发送协调者消息
 *              else
 *                  等待协调者消息；
 *                  if 收到协调者消息
 *                      then 设置协调者并将自己设置为follower
 *                  else 进入2.2循环
 *
 *      endif
 * 3. 故障检测
 *  3.1 定时与协调者发起心跳消息进行故障检测
 *      在3T时间内（实现为3次吧，每次T秒钟超时时间）没有响应则认为协调者故障
 *      算法：
 *      send 心跳消息，等待协调者回复
 *      if 心跳消息超时，故障计数+1
 *          if 故障计数 >=3：协调者故障
 *              发起选举；
 *           else
 *              等待下次定时器调用
 *       else
 *          收到了服务；
 *          故障计数清零；
 *
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/09
 */
public class BullyElectionAlgorithm {
    /**
     * 配置文件
     */
    private final BullyConfig bullyConfig;

    /**
     * 网络服务器
     */
    private NetworkServer networkServer;

    /**
     * 网络客户端
     */
    private NetworkClient networkClient;

    /**
     * 故障检测器
     */
    private FaultDetector faultDetector;

    /**
     * 节点管理器
     */
    private NodeManager nodeManager;

    /**
     * 构造函数
     * @param bullyConfig 霸道选举算法配置
     */
    public BullyElectionAlgorithm(BullyConfig bullyConfig) {
        this.bullyConfig = bullyConfig;

    }

    /**
     * 初始化各个模块
     */
    public void init() {
        this.networkServer = new NettyNetworkServer(bullyConfig.getNettyServerConfig());
        this.networkClient = new NettyNetworkClient(bullyConfig.getNettyClientConfig());
        // 注册选举消息处理器
        this.networkServer.registerProcessor(RequestCode.ELECTION.getCode(), new ElectionProcessor(this), null);
        this.networkServer.registerProcessor(RequestCode.COORDINATOR.getCode(), new CoordinatorProcessor(this), null);
        this.networkServer.registerProcessor(RequestCode.HEART_BEAT.getCode(), new HeartBeatProcessor(this), null);
        this.nodeManager = new DefaultNodeManager(bullyConfig, networkClient);
        this.nodeManager.initialize();
        this.faultDetector = new DefaultFaultDetector(this);
    }

    public void start() {
        // 启动网络服务器
        networkServer.start();
        // 启动网络客户端
        networkClient.start();
        // 启动故障检测器
        faultDetector.start();
        // 启动节点管理器
        nodeManager.start();
    }

    public void shutdown() {
        networkServer.shutdown();
        networkClient.shutdown();
        faultDetector.shutdown();
        nodeManager.shutdown();
    }

    static void createServer() {
        NettyServerConfig nettyServerConfig = new NettyServerConfig();
        nettyServerConfig.setListenPort(60000);
        NetworkServer server = new NettyNetworkServer(nettyServerConfig);
        // 注册消息处理器
        server.registerProcessor(RequestCode.ELECTION.getCode(), new HelloWorldServerProcessor(server), null);
        server.start();
    }

    static void createClient() {
        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        NettyNetworkClient networkClient = new NettyNetworkClient(nettyClientConfig, null);
        networkClient.registerProcessor(RequestCode.ELECTION_ACK.getCode(), new HelloWorldClientProcessor(), null);
        networkClient.start();

        HelloWorldRequestHeader helloWorldHeader = new HelloWorldRequestHeader();
        helloWorldHeader.setEcho("Hi, Server: Hello World!");
        NetworkCommand request = NetworkCommand.createRequestCommand(RequestCode.ELECTION.getCode(), helloWorldHeader);
        try {
            networkClient.sendOneway("127.0.0.1:60000", request, 30L * 1000, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NetworkTimeoutException e) {
            e.printStackTrace();
        } catch (NetworkSendRequestException e) {
            e.printStackTrace();
        } catch (NetworkConnectException e) {
            e.printStackTrace();
        }
    }

    public BullyConfig getBullyConfig() {
        return bullyConfig;
    }

    public NetworkServer getNetworkServer() {
        return networkServer;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public FaultDetector getFaultDetector() {
        return faultDetector;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }


}
