//package com.github.futurefs.store.block;
//
//import com.github.futurefs.netty.ResultCode;
//import com.github.futurefs.store.common.AppendResult;
//import com.github.futurefs.store.common.constant.StoreConstant;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Random;
//
///**
// * 文件块测试类
// *
// * @author errorfatal89@gmail.com
// * @datetime 2022/03/25 15:03
// */
//public class BlockFileTest {
//
//    @Test
//    public void testWrite() {
//        String path = "demo.dir/1";
//        BlockFile blockFile = new BlockFile(new File(path));
//        blockFile.init();
//
//        FileBlock fileBlock = new FileBlock();
//        FileHeader header = new FileHeader();
//        header.setHeaderMagic(StoreConstant.BLOCK_HEADER_MAGIC_NUMBER);
//        header.setKey(new Random().nextLong());
//        header.setDeleteStatus(0);
//        fileBlock.setHeader(header);
//
//        byte[] body = readFile();
//        fileBlock.setBody(body);
//
//        FileTailor fileTailor = new FileTailor();
//        fileTailor.setTailorMagic(StoreConstant.BLOCK_TAILOR_MAGIC_NUMBER);
//        fileBlock.setTailor(fileTailor);
//
//        AppendResult appendResult = blockFile.append(fileBlock);
//        Assert.assertEquals(appendResult.getResult(), ResultCode.SUCCESS);
//    }
//
//    private byte[] readFile() {
//        String path = "G:\\github.com\\fofcn\\operation-system\\os\\lib\\ehcache-3.8.1.jar";
//        File file = new File(path);
//        byte[] content = new byte[(int) file.length()];
//        try (InputStream in = new FileInputStream(file)) {
//            in.read(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return content;
//    }
//}
