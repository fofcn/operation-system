package fs.trivial.inodetable;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import fs.trivial.root.RootDirectoryManager;
import fs.trivial.superblock.SuperBlockManager;
import lang.serializer.ByteArraySerializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * file name and inode map manager
 *
 * @author jiquanxi
 * @date 2021/12/29
 */
public class FileNameInodeManager implements Manager {
    private final CaSystem caSystem;

    private volatile long dataStartPage;

    private volatile long dataStartOffset;

    private volatile long dataEndOffset;

    private volatile long dataPages;

    private final long fileInodeSize = 8 + 4 + SuperBlockManager.MAX_PATH;

    public FileNameInodeManager(final CaSystem caSystem) {
        this.caSystem = caSystem;

    }

    @Override
    public boolean initialize() {
        this.dataStartPage = caSystem.getSuperBlockManager().getFileInodeStartPage();
        this.dataPages = caSystem.getSuperBlockManager().getFileInodePages();
        this.dataStartOffset = dataStartPage * caSystem.getBlockSize();
        this.dataEndOffset = dataStartOffset + dataPages * caSystem.getBlockSize();
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public long getInodeNumber(String name) {
        if (caSystem.getSuperBlockManager().getInodeAmount() == 0L) {
            return -1L;
        }

        long offset = dataStartPage * caSystem.getBlockSize();
        long end = offset + dataPages * caSystem.getBlockSize();

        for (long i = offset; i <= end;  i = i + fileInodeSize) {
            FileNameInode nameInode = readFileNameInode(i);
            if (name.equals(nameInode.getName())) {
                return nameInode.getInodeNumber();
            }
        }

        return -1L;
    }

    public int createFileInode(int index, long inodeNumber, String name) {
        // 根据位图索引定位到硬盘偏移位置
        long startOffset = index * fileInodeSize + dataStartOffset;
        if (startOffset >= dataEndOffset) {
            // 范围超出最大偏移
            return -1;
        }

        // 创建i-node和file name映射对象
        FileNameInode fileNameInode = new FileNameInode();
        fileNameInode.setInodeNumber(inodeNumber);
        fileNameInode.setNameLength(name.length());
        fileNameInode.setName(name);
        // 序列化到硬盘中

        byte[] newFileInode = ByteArraySerializer.serialize(fileNameInode, FileNameInode.class);
        caSystem.getDiskHelper().write(newFileInode, startOffset);
        // 期间产生错误就报错
        return 0;
    }

    /**
     * 获取文件名列表
     * @return 文件名列表，无文件则返回空数组
     */
    public List<String> getFileList() {
        List<String> result = new ArrayList<>();
        for (long i = dataStartOffset, j = 0; i < dataEndOffset && j < caSystem.getSuperBlockManager().getInodeAmount(); i = i + fileInodeSize, j++) {
            FileNameInode nameInode = readFileNameInode(i);
            result.add(RootDirectoryManager.ROOT_DIR + nameInode.getName());
        }

        return result;
    }

    private FileNameInode readFileNameInode(long offset) {
        byte[] bytes = caSystem.getDiskHelper().read(offset, 12);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long inodeNumber = buffer.getLong();
        int nameLength = buffer.getInt();
        bytes = caSystem.getDiskHelper().read(offset + 12, nameLength);
        String fileName = new String(bytes, StandardCharsets.UTF_8);

        FileNameInode fileNameInode = new FileNameInode();
        fileNameInode.setName(fileName);
        fileNameInode.setNameLength(nameLength);
        fileNameInode.setInodeNumber(inodeNumber);

        return fileNameInode;
    }
}
