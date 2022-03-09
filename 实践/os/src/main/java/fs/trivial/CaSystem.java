package fs.trivial;

import fs.FileSystem;
import fs.helper.DiskHelper;
import fs.trivial.boot.BootBlockManager;
import fs.trivial.freespace.BitMapFreeSpaceManager;
import fs.trivial.freespace.GetFreeSpaceIndexes;
import fs.trivial.inode.Inode;
import fs.trivial.inode.InodeManager;
import fs.trivial.inodebitmap.InodeBitMapManager;
import fs.trivial.inodetable.FileNameInodeManager;
import fs.trivial.root.RootDirectoryManager;
import fs.trivial.superblock.SuperBlockManager;
import util.StdOut;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * Continuous allocation file system.
 *
 * Algorithm for Continuous allocation file system:
 * disk layout:
 * ------------------------------------------------------
 * |MBR | partition table| partition 1 | partition 2|...|
 * -----------------------------------------------------
 *
 * partition layout:
 * -------------------------------------------------------------------------------------------------------------------------------------------
 * | boot block | super block | root directory | i-node bit map | i-node | free space management| file&i-node bit map | file&i-node | data|
 * ------------------------------------------------------------------------------------------------------------------------------------------
 * @author errorfatal89@gmail.com
 * @date 2021/12/27
 */
public class CaSystem implements FileSystem {

    private final String diskPath;

    private final Partition partition;

    private final int blockSize;

    private DiskHelper diskHelper;

    private BitMapFreeSpaceManager freeSpaceManager;

    private BootBlockManager bootBlockManager;

    private SuperBlockManager superBlockManager;

    private RootDirectoryManager rootDirectoryManager;

    private InodeBitMapManager inodeBitMapManager;

    private InodeManager inodeManager;

    private FileNameInodeManager fileNameInodeManager;

    private DataManager dataManager;

    public CaSystem(final String diskPath, Partition partition, final int blockSize) {
        this.diskPath = diskPath;
        this.partition = partition;
        this.blockSize = blockSize;

        long partitionLength = partition.getEnd() - partition.getStart();
        try {
            this.diskHelper = new DiskHelper(diskPath, partitionLength);
        } catch (IOException e) {
            StdOut.println(e);
        }

        this.bootBlockManager = new BootBlockManager(this);
        this.superBlockManager = new SuperBlockManager(this);
        this.rootDirectoryManager = new RootDirectoryManager(this);
        this.inodeBitMapManager = new InodeBitMapManager(this);
        this.inodeManager = new InodeManager(this);
        this.freeSpaceManager = new BitMapFreeSpaceManager(this);
        this.fileNameInodeManager = new FileNameInodeManager(this);
        this.dataManager = new DataManager(this);
    }

    /**
     * add a disk and init system info
     * MBR,partition information,boot block,super block,
     * free space management,i-node,root directory,files and directories
     */
    public void initialize() {
        bootBlockManager.initialize();
        superBlockManager.initialize();
        rootDirectoryManager.initialize();
        rootDirectoryManager.initialize();
        inodeBitMapManager.initialize();
        inodeManager.initialize();
        freeSpaceManager.initialize();
        fileNameInodeManager.initialize();
        dataManager.initialize();
        bootBlockManager.hasInitialized();
    }

    public long createFile(String name) {
        // 在i-node和file name映射关系中查找文件是否存在
        long inodeNumber = fileNameInodeManager.getInodeNumber(name);
        // 根据i-node number查找i-node
        // 如果i-node不存在，则查找i-node位图，根据位图索引在i-node区创建inode
        // 如果i-node存在，则查看i-node是否删除，如果删除则可以直接复用该i-node，
        // 并重置文件元数据
        if (inodeNumber != -1L) {
            // todo 错误处理：文件已经存在
            boolean isDeleted = inodeManager.isInodeDeleted(inodeNumber);
            if (!isDeleted) {
                return -2L;
            }

        }

        // 查找i-node 位图是否有空的i-node节点位置
        int index = inodeBitMapManager.getFreeInodeIndex();
        if (index == -1) {
            // todo 错误处理：位图中无空闲位表示i-node数量已经被使用完
            // 或代码逻辑错误-^-
            return -3L;
        }

        // 创建i-node节点
        inodeNumber = inodeManager.createInode(index);
        if (inodeNumber == -1L) {
            // todo 错误处理：i-node节点创建失败
            return -4L;
        }

        // 创建i-node和filename的映射关系;
        // 在i-node和filename的映射区新建关系
        // index * (file&i-node).maxLength为数据区偏移起始点
        fileNameInodeManager.createFileInode(index, inodeNumber, name);
        // todo 错误处理：创建i-node和file name 映射失败

        // 更新i-node位图为已使用
        inodeBitMapManager.setBlockUsed(index);

        // 更新super block inode统计
        superBlockManager.incrementInodeAmount();

        return inodeNumber;
    }

    public long openFile(String name) throws FileNotFoundException {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("file name.");
        }

        long inodeNumber = fileNameInodeManager.getInodeNumber(name);
        if (inodeNumber == -1L) {
            throw new FileNotFoundException(name);
        }

        // 将i-node信息读取缓存中
        boolean isDeleted = inodeManager.isInodeDeleted(inodeNumber);
        if (isDeleted) {
            throw new FileNotFoundException(name);
        }

        return inodeNumber;
    }

    public long writeFile(long inodeNumber, byte[] content) throws FileNotFoundException, FileAlreadyExistsException {
        if (inodeNumber < 0 || content == null) {
            throw new IllegalArgumentException();
        }

        // 根据i-node number查找i-node信息
        // 先在缓存查找i-node信息
        // 如果无法找到则回硬盘查找
        // 在磁盘找到了就写到缓存中
        Inode inode = inodeManager.getInode(inodeNumber);
        if (inode == null) {
            throw new FileNotFoundException("未找到文件");
        }

        if (inode.getLength() > 0) {
            throw new FileAlreadyExistsException("文件已存在");
        }

        // 根据内容长度计算块的数量
        int blocks = content.length / blockSize + (content.length % blockSize == 0 ? 0 : 1);

        // 从空闲区管理中查找空闲块,如果空闲块不足则直接报错：No space left.
        GetFreeSpaceIndexes result = freeSpaceManager.getFreeIndexes(blocks);
        if (result == null) {
            throw new Error("No space left.");
        }

        // 将内容写入空闲块并刷盘
        dataManager.writeData(content, result.getStart(), result.getEnd());
        // 更新空闲块为已使用并刷盘
        freeSpaceManager.setBlockUsed(result.getStart(), result.getEnd());
        // 更新i-node信息长度并刷盘
        inodeManager.updateOnWrite(inode, content.length, result.getStart());

        return 0L;
    }

    public byte[] readFile(long inodeNumber) throws FileNotFoundException {
        if (inodeNumber < 0) {
            throw new IllegalArgumentException();
        }

        Inode inode = inodeManager.getInode(inodeNumber);
        if (inode == null) {
            throw new FileNotFoundException("未找到文件");
        }

        return dataManager.readData(inode);
    }

    public List<String> getFileList() {
        return fileNameInodeManager.getFileList();
    }

    public DiskHelper getDiskHelper() {
        return diskHelper;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public BootBlockManager getBootBlockManager() {
        return bootBlockManager;
    }

    public long getPartitionSize() {
        return partition.getEnd() - partition.getStart();
    }

    public InodeManager getInodeManager() {
        return inodeManager;
    }

    public SuperBlockManager getSuperBlockManager() {
        return superBlockManager;
    }


}
