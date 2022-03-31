package com.github.futurefs.client.api;

import com.github.futurefs.client.api.config.ClientConfig;
import com.github.futurefs.netty.config.NettyClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端测试类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 17:21
 */
public class TrickyClientTest {

    private static TrickyClient trickyClient;

    private static ClientManager clientManager;

    @BeforeClass
    public static void beforeClass() {
        List<String> storeNodes = new ArrayList<>();
        storeNodes.add("127.0.0.1:60000");
        ClientConfig clientConfig = ClientConfig.builder()
                .storeNodes(storeNodes)
                .threadCnt(2)
                .tcpClientConfig(new NettyClientConfig())
                .build();
        clientManager = new ClientManager(clientConfig);
        clientManager.init();
        trickyClient = clientManager.getClient();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        clientManager.shutdown();
    }

    @Test
    public void testWrite() throws IOException {
        File file = new File("G:\\github.com\\html-exporter-master.zip");
        InputStream in = new FileInputStream(file);
        byte[] content = new byte[in.available()];
        in.read(content);
        ApiResult<Long> fileKey = trickyClient.write("", content);
        if (ApiResultWrapper.isSuccess(fileKey)) {
            System.out.println(fileKey.getData());
        }
    }
}
