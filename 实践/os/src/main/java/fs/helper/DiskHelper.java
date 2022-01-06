package fs.helper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A tool for alloc virtual disk.
 *
 * @author jiquanxi
 * @date 2021/12/27
 */
public class DiskHelper {
    private final String fileName;
    private final long fileSize;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;

    public DiskHelper(final String fileName, final long fileSize) throws IOException {
        this.fileName = fileName;
        this.fileSize = fileSize;

        // 内存映射
        File file = new File(fileName);
        try {
            this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileChannel != null) {
                this.fileChannel.close();
            }

        }
    }

    public byte[] read(long offset, int length) {
        ByteBuffer buffer = mappedByteBuffer.slice();
        buffer.position((int) offset);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        buffer.clear();
        return bytes;
    }

    public void write(byte[] bytes, long offset) {
        ByteBuffer buffer = mappedByteBuffer.slice();
        buffer.position((int) offset);
        buffer.put(bytes);
        mappedByteBuffer.force();
    }

    public void write(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = mappedByteBuffer.slice();
        buffer.position(offset);
        buffer.put(bytes, offset, length);
        mappedByteBuffer.force();
    }

    public void shutdown() {
        try {
            if (fileChannel != null) {
                fileChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
