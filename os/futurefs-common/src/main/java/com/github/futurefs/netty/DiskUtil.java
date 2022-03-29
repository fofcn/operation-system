package com.github.futurefs.netty;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 磁盘工具类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:55
 */
public class DiskUtil {

    /**
     * 获取硬盘空间信息
     * @param dir 目录
     * @return
     * @throws IOException
     */
    public static Map<String, Long> getDiskSpace(String dir) throws IOException {
        File file = new File(dir);
        if (!file.exists()) {
            throw new IOException("dir not found: " + dir);
        }

        long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();
        long usableSpace = file.getUsableSpace();

        Map<String, Long> countTable = new HashMap<>(3);
        countTable.put("totalSpace", totalSpace);
        countTable.put("freeSpace", freeSpace);
        countTable.put("usableSpace", usableSpace);
        return countTable;
    }


}
