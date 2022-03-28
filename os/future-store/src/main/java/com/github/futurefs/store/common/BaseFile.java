package com.github.futurefs.store.common;

import com.github.futurefs.netty.FilePaddingUtil;
import com.github.futurefs.netty.R;
import com.github.futurefs.netty.RWrapper;
import com.github.futurefs.store.common.constant.StoreConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 存储文件
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/24 15:10
 */
@Slf4j
public class BaseFile {
    /**
     * 文件路径
     */
    private final File file;

    /**
     * 文件句柄
     */
    private volatile RandomAccessFile rasFile;

    /**
     * 文件管道
     */
    private volatile FileChannel fileChannel;

    /**
     * 当前写入位置,初始化为4096
     */
    private final AtomicLong writePos = new AtomicLong(4096L);

    /**
     * 预分配位置
     */
    private final AtomicLong paddingPos = new AtomicLong(0L);

    /**
     * 读写锁
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 自动扩展大小
     * todo 配置化
     */
    private final long AUTO_EXPAND_SIZE = 1024L * 1024;

    public BaseFile(final File file) {
        this.file = file;
    }

    /**
     * 验证文件是否为存储文件
     * @return true:是，false:否
     */
    public boolean valid() {
        try {
            long magic = rasFile.readLong();
            if (StoreConstant.INDEX_SUPER_MAGIC_NUMBER == magic) {
                return true;
            }
        } catch (IOException e) {
            log.error("read file error");
            return false;
        }

        return false;
    }

    /**
     * 文件初始化
     */
    public boolean init() {
        doBeforeInit();
        boolean newFile = false;
        try {
            if (!file.getParentFile().exists()) {
                Files.createDirectories(Paths.get(file.getParentFile().getAbsolutePath()));
                newFile = true;
            }

            if (!file.exists()) {
                Files.createFile(Paths.get(file.getAbsolutePath()));
                newFile = true;
            }

            this.rasFile = new RandomAccessFile(file, "rw");
            fileChannel = rasFile.getChannel();
            // 如果是新文件，那么执行新文件初始化
            if (newFile) {
                doInitNewFile();
            } else {
                doInitOldFile();
                doRecover();
            }
            // 刷盘
            fileChannel.force(false);
            doAfterInit();
        } catch (IOException e) {
            throw new IllegalArgumentException("file not found, file path: " + file.getAbsolutePath());
        }

        return true;
    }

    /**
     * 关闭
     */
    public void close() {
        try {
            fileChannel.close();
            rasFile.close();
        } catch (IOException e) {
            log.error("random access file close error.", e);
        }
    }

    /**
     * 追加文件
     * @param buffer
     * @return 追加结果
     */
    public R<AppendResult> append(ByteBuffer buffer) {
        AppendResult appendResult = new AppendResult();
        if (readWriteLock.writeLock().tryLock()) {
            int length = buffer.limit();
            padding(length);

            long pos;
            try {
                // 取当前写入偏移
                pos = writePos.get();
                // 写入数据内容
                fileChannel.write(buffer, pos);
                pos = writePos.addAndGet(length);

                doAfterAppend(pos, length);
            } catch (IOException e) {
                log.error("write file error.", e);
                return RWrapper.fail();
            } finally {
                readWriteLock.writeLock().unlock();
            }

            appendResult.setOffset(pos);
            return RWrapper.success(appendResult);
        }

        log.error("acquire write lock error");
        return RWrapper.fail();
    }

    /**
     * 读取文件
     * @param pos 读取偏移
     * @param length 读取长度
     * @return 读取结果
     */
    public ByteBuffer read(long pos, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        if (readWriteLock.readLock().tryLock()) {
            try {
                fileChannel.position(pos);
                fileChannel.read(buffer);
                return buffer;
            } catch (IOException e) {
                log.error("io error", e);
            } finally {
                readWriteLock.readLock().unlock();
            }
        }

        return null;
    }

    private void padding(long startPos, long length) {
        try {
            FilePaddingUtil.padFile(fileChannel, startPos + length);
            paddingPos.addAndGet(length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void padding(int contentLength) {
        long diff = paddingPos.get() - writePos.get();
        while(diff < AUTO_EXPAND_SIZE || contentLength - diff > 0) {
            padding(paddingPos.get(), AUTO_EXPAND_SIZE);
            diff = paddingPos.get() - writePos.get();
        }
    }

    protected void doInitNewFile() throws IOException {

    }

    protected void doRecover() throws IOException {

    }

    protected void doInitOldFile() throws IOException {

    }

    protected void doBeforeInit() {

    }

    protected void doAfterInit() {

    }

    /**
     * 主文件添加完成后的动作
     * @param offset 偏移
     * @param length 写入长度
     */
    protected void doAfterAppend(long offset, int length) {
    }

    protected void incrPaddingPos(long incr) {
        paddingPos.addAndGet(incr);
    }

    protected void padFile(long toPos) throws IOException {
        FilePaddingUtil.padFile(fileChannel, toPos);
        incrPaddingPos(toPos);
    }

    protected void writeLong(long offset, long data) throws IOException {
        rasFile.seek(offset);
        rasFile.writeLong(data);
    }

    protected void writeLong(long data) throws IOException {
        rasFile.writeLong(data);
    }

    protected long readLong(long pos) throws IOException {
        rasFile.seek(pos);
        return rasFile.readLong();
    }

    protected long readLong() throws IOException {
        return rasFile.readLong();
    }

    protected void resetWritePos(long newPos) throws IOException {
        writePos.set(newPos);
        padding(newPos, 0L);
    }

    protected MappedByteBuffer map(int startPos, int size) throws IOException {
        return fileChannel.map(FileChannel.MapMode.READ_WRITE, startPos, size);
    }

    protected void flush() throws IOException {
        fileChannel.force(false);
    }
}
