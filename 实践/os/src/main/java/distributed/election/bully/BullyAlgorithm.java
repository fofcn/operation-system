package distributed.election.bully;

import distributed.election.bully.command.HelloWorldRequestHeader;
import distributed.election.bully.processor.HelloWorldClientProcessor;
import distributed.election.bully.processor.HelloWorldServerProcessor;
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
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/09
 */
public class BullyAlgorithm {
    public static void main(String[] args) {
        createServer();

        createClient();
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
            networkClient.sendSync("127.0.0.1:60000", request, 30L * 1000);
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
}
