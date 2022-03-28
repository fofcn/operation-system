package com.github.futurefs.client;

import com.github.futurefs.netty.FileDataProtos;
import com.github.futurefs.netty.NettyProtos;
import com.github.futurefs.netty.config.NettyClientConfig;
import com.github.futurefs.netty.exception.NetworkConnectException;
import com.github.futurefs.netty.exception.NetworkSendRequestException;
import com.github.futurefs.netty.exception.NetworkTimeoutException;
import com.github.futurefs.netty.netty.CommandCustomHeader;
import com.github.futurefs.netty.netty.NettyNetworkClient;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.network.RequestCode;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 客户端控制器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 18:13
 */
public class ClientController {

    private final NettyNetworkClient networkClient;

    public ClientController(NettyClientConfig clientConfig) {
        this.networkClient = new NettyNetworkClient(clientConfig);
    }

    public void init() {

    }

    public void start() {
        networkClient.start();
    }

    public void sendFile() {
        File file = new File("G:\\github.com\\fofcn\\operation-system\\LICENSE");
        byte[] content = new byte[(int) file.length()];
        try (InputStream in = new FileInputStream(file)) {
            in.read(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileDataProtos.FileRequest fileRequest = FileDataProtos.FileRequest.newBuilder()
                .setLength(file.length())
                .setData(ByteString.copyFrom(content)).build();
        NettyProtos.NettyRequest request = NettyProtos.NettyRequest.newBuilder().setFileRequest(fileRequest).build();
        NetworkCommand command = NetworkCommand.createRequestCommand(RequestCode.FILE_UPLOAD, new CommandCustomHeader() {
            @Override
            public int getCode() {
                return 0;
            }
        });
        command.setBody(request.toByteArray());
        try {
            networkClient.sendSync("127.0.0.1:60000", command, 30 * 1000L);
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

    public void shutdown() {
        networkClient.shutdown();
    }

    public static void main(String[] args) {
        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        ClientController controller = new ClientController(nettyClientConfig);
        controller.init();
        controller.start();
        controller.sendFile();
    }
}
