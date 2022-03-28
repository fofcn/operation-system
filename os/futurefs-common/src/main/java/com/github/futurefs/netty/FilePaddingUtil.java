package com.github.futurefs.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件预分配工具类
 * 参考zookeeper FilePadding 实现
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 11:54
 */
public class FilePaddingUtil {

    private static final ByteBuffer FILL = ByteBuffer.allocateDirect(1);

    /**
     * 预分配文件
     * @param fileChannel fileChannel
     * @param toPos 起始偏移
     * @throws IOException IO异常
     */
    public static void padFile(FileChannel fileChannel, long toPos) throws IOException {
        fileChannel.write((ByteBuffer) FILL.position(0), toPos);
    }
}
