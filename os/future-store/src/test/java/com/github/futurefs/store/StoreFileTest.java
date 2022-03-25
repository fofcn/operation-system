package com.github.futurefs.store;

import com.github.futurefs.proto.StoreFile;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * protocol buffers存储文件测试
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/22
 */
public class StoreFileTest {
    private final String writeStr = "abcdefg";

    private static final String binFile = "demo.bin";

    @AfterClass
    public static void afterClass() {
        try {
            Files.delete(Paths.get(binFile));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("文件删除失败");
        }
    }

    @Test
    public void writeFile() throws IOException {
        FileOutputStream output = new FileOutputStream(binFile);
        for (int i = 0; i < 100; i++) {
            StoreFile.FileHeader header = StoreFile.FileHeader.newBuilder()
                    .setHeaderMagic(0x0000000000000000)
                    .setCreateTime(i)
                    .setDeleteTime(i)
                    .setDeleteStatus(0L)
                    .setCrc64Number(0L)
                    .setKey(i)
                    .setLength(writeStr.length()).build();

            ByteString bytes = ByteString.copyFrom(writeStr.getBytes(StandardCharsets.UTF_8));
            StoreFile.FileBody body = StoreFile.FileBody.newBuilder().setBody(bytes).build();
            StoreFile.FileTailor tailor = StoreFile.FileTailor.newBuilder()
                    .setTailMagic(0x0000000000000000).build();

            StoreFile.FileStructure file = StoreFile.FileStructure.newBuilder()
                    .setHeader(header)
                    .setBody(body)
                    .setTailor(tailor).build();

            file.writeTo(output);
        }
        output.close();
    }

    @Test
    public void readFile() throws InvalidProtocolBufferException {
        File file = new File(binFile);
        if (!file.exists()) {
            return;
        }

        byte[] part = new byte[4096];
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(binFile))) {
            int len;
            while ((len = in.read(part)) != -1) {
                bos.write(part, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StoreFile.FileStructure fileStructure = StoreFile.FileStructure.parseFrom(bos.toByteArray());
        String str = new String(fileStructure.getBody().getBody().toByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals(str, writeStr);
    }

    @Test
    public void readPartial() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File(binFile), "rw");
        file.seek(57L);
        InputStream is = Channels.newInputStream(file.getChannel());
        StoreFile.FileHeader fileHeader = StoreFile.FileHeader.parseFrom(is);
        System.out.println(fileHeader.getCreateTime());
    }
}
