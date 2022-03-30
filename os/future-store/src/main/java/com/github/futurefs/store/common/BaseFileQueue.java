package com.github.futurefs.store.common;

import com.github.futurefs.store.config.StoreConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 存储文件
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/24 13:47
 */
@Slf4j
public class BaseFileQueue {
    /**
     * 文件表
     */
    private final ConcurrentHashMap<String, BaseFile> fileTable = new ConcurrentHashMap<>(256);

    /**
     * 存储配置
     */
    private final StoreConfig storeConfig;

    /**
     * 写入偏移
     */
    private final AtomicLong writePos = new AtomicLong(0L);

    /**
     * 读写锁
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 文件名称生成器
     */
    private BaseFileNameGenerator fileNameGenerator;

    public BaseFileQueue(final StoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }

    /**
     * 文件初始化
     */
    public boolean init() {
        try {
            // 目录不存在则创建目录
            File dirFile = new File(storeConfig.getBlockPath());
            if (!dirFile.exists()) {
                Files.createDirectories(Paths.get(storeConfig.getBlockPath()));
            }

            // 遍历目录下文件列表
            File[] storeFiles = dirFile.listFiles();
            if (ArrayUtils.isEmpty(storeFiles)) {
                // 加载验证是否为存储文件
                for (File storeFile : storeFiles) {
                    if (storeFile.isDirectory()) {
                        continue;
                    }

                    BaseFile baseFile = new BaseFile(storeFile);
                    boolean isValid = baseFile.valid();
                    if (isValid) {
                        // 添加到文件列表
                        fileTable.put(storeFile.getName(), baseFile);
                    } else {
                        log.warn("store directory contains dirty file, file name:<{}>", storeFile.getAbsolutePath());
                    }
                }
            } else {
                // 创建一个新的存储文件
                String name = fileNameGenerator.generator();
                File file = new File(storeConfig.getBlockPath() + File.pathSeparator + name);
                BaseFile baseFile = new BaseFile(file);
                // 添加到文件列表
                fileTable.put(file.getName(), baseFile);
            }
            return true;
        } catch (IOException e) {
            log.error("store file error.", e);
        }

        return false;
    }

    /**
     * 关闭
     */
    public void shutdown() {
       fileTable.entrySet().stream().forEach(entry -> {entry.getValue().close();});
    }

    public void setFileNameGenerator(BaseFileNameGenerator fileNameGenerator) {
        this.fileNameGenerator = fileNameGenerator;
    }
}
