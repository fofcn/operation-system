//package com.github.futurefs.store.common;
//
//import com.github.futurefs.store.config.StoreConfig;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// * 支持文件测试类
// *
// * @author errorfatal89@gmail.com
// * @datetime 2022/03/24 14:44
// */
//public class BaseFileQueueTest {
//    private static final String dir = "demo.store";
//    private static BaseFileQueue baseFile;
//
//    @BeforeClass
//    public static void beforeClass() {
//        StoreConfig storeConfig = new StoreConfig();
//        storeConfig.setDir(dir);
//        baseFile = new BaseFileQueue(storeConfig);
//    }
//
//    @AfterClass
//    public static void afterClass() {
//        baseFile.close();
//    }
//
//    @Test
//    public void testInit() {
//        baseFile.init();
//    }
//}
