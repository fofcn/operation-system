package com.github.futurefs.client.api.rpc;

import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.NetworkClient;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.exception.NetworkConnectException;
import com.github.futurefs.netty.exception.NetworkSendRequestException;
import com.github.futurefs.netty.exception.NetworkTimeoutException;
import com.github.futurefs.netty.exception.TrickyFsNetworkException;
import com.github.futurefs.netty.netty.NettyNetworkClient;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;

/**
 * Rpc客户端
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 14:33
 */
@Slf4j
public class RpcClient {

    private final NetworkClient networkClient;

    private final long waitMillis;

    private final String address;

    public RpcClient(final NettyClientConfig nettyClientConfig, String address) {
        this.networkClient = new NettyNetworkClient(nettyClientConfig);
        this.address = address;
        this.networkClient.start();
        this.waitMillis = nettyClientConfig.getConnectTimeoutMillis();
    }

    /**
     * 同步调用
     * @param requestCode 请求码
     * @param request 请求
     * @return 结果
     */
    public NettyProtos.NettyReply callSync(int requestCode, NettyProtos.NettyRequest request) throws TrickyFsNetworkException {
        NetworkCommand networkCommand = NetworkCommand.createRequestCommand(requestCode, request.toByteArray());
        NetworkCommand response = null;
        try {
            response = networkClient.sendSync(address, networkCommand, waitMillis);
            if (NetworkCommand.isResponseOk(response)) {
                return NettyProtos.NettyReply.parseFrom(response.getBody());
            }

            return null;
        } catch (InterruptedException e) {
            throw new TrickyFsNetworkException(e);
        } catch (NetworkTimeoutException e) {
            throw new TrickyFsNetworkException(e);
        } catch (NetworkSendRequestException e) {
            throw new TrickyFsNetworkException(e);
        } catch (NetworkConnectException e) {
            throw new TrickyFsNetworkException(e);
        } catch (InvalidProtocolBufferException e) {
            throw new TrickyFsNetworkException(e);
        }

    }

    public void shutdown() {
        networkClient.shutdown();
    }
}
