package com.github.futurefs.store.common;

import com.github.futurefs.common.ResultCode;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基础存储文件测试类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 11:32
 */
public class BaseFileTest {

    private static BaseFile baseFile;

    private static final String DIR = "demo.dir";

    private static final String FILE_NAME = "0";

    private static final String PATH = DIR + File.separator + FILE_NAME;

    private static final File STORE_FILE = new File(PATH);

    @BeforeClass
    public static void beforeClass() {
        File file = new File(DIR);
        if (!file.exists()) {
            file.mkdirs();
        }


        baseFile = new BaseFile(STORE_FILE);
        boolean initResult = baseFile.init();
        Assert.assertTrue(initResult);
    }

    @AfterClass
    public static void afterClass() {
        baseFile.close();
//        STORE_FILE.delete();
    }

    @Test
    public void testAppendAndRead() {
        ConcurrentHashMap<Long, String> resultTable = new ConcurrentHashMap<>(1000);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            executor.execute(() -> {
                try {
                    String uuid = UUID.randomUUID().toString();
                    ByteBuffer buffer = ByteBuffer.allocate(uuid.length());
                    buffer.put(uuid.getBytes(StandardCharsets.UTF_8));
                    AppendResult appendResult = baseFile.append(buffer);
                    if (appendResult.getResult() == ResultCode.SUCCESS) {
                        resultTable.put(appendResult.getOffset(), uuid);
                    } else {
                        Assert.fail("文件写入失败");
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(resultTable.size(), 1000);

        resultTable.entrySet().forEach(entry -> {
            ByteBuffer buffer = baseFile.read(entry.getKey(), entry.getValue().length());
            Assert.assertNotNull(buffer);

            String uuid = new String(buffer.array(), StandardCharsets.UTF_8);
            Assert.assertEquals(uuid, entry.getValue());
        });
    }

}
