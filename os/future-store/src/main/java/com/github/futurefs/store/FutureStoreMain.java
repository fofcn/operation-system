package com.github.futurefs.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.futurefs.netty.YamlUtil;
import com.github.futurefs.store.config.StoreConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储主函数
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:27:00
 */
@Slf4j
public class FutureStoreMain {

    public static void main(String[] args) {
        InputStream in = FutureStoreMain.class.getResourceAsStream("/store.yml");
        if (ObjectUtils.isEmpty(in)) {
            log.error("config file not found");
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
