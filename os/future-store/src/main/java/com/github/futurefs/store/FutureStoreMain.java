package com.github.futurefs.store;

import com.github.futurefs.netty.YamlUtil;
import com.github.futurefs.store.config.StoreConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 存储主函数
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:27:00
 */
@Slf4j
public class FutureStoreMain {

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("miss start up arguments: configFile");
            return;
        }

        File confFile = new File(args[0]);
        if (!confFile.exists()) {
            log.error("start up config not found: <{}>", args[0]);
            return;
        }

        InputStream in = null;
        try {
            in = new FileInputStream(confFile);
        } catch (FileNotFoundException e) {
            log.error("start up config open failed: <{}>", args[0]);
            return;
        }

        StoreConfig storeConfig = YamlUtil.readObject(in, StoreConfig.class, "trickyfs");
        StoreController controller = new StoreController(storeConfig);
        controller.init();
        controller.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("start shutdown hook");
            controller.shutdown();
            log.info("end shutdown hook");
        }, "shutdown-hook"));
    }
}
