package fs.trivial;

import fs.FileSystem;
import fs.helper.DiskHelper;
import fs.trivial.boot.BootBlockManager;
import fs.trivial.freespace.FreeSpaceManager;
import fs.trivial.inode.InodeManager;
import fs.trivial.inodebitmap.InodeBitMapManager;
import fs.trivial.inodetable.FileNameInodeManager;
import fs.trivial.root.RootDirectoryManager;
import fs.trivial.superblock.SuperBlockManager;

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
 * @author jiquanxi
 * @date 2021/12/27
 */
public class CaSystem implements FileSystem {


    private final Partition partition;

    private final int blockSize;

    private FreeSpaceManager freeSpaceManager;

    private DiskHelper diskHelper;

    private BootBlockManager bootBlockManager;

    private SuperBlockManager superBlockManager;

    private InodeBitMapManager inodeBitMapManager;

    private InodeManager inodeManager;

    private RootDirectoryManager rootDirectoryManager;

    private FileNameInodeManager fileNameInodeManager;

    public CaSystem(Partition partition, final int blockSize) {
        this.partition = partition;
        this.blockSize = blockSize;
    }

    /**
     * add a disk and init system info
     * MBR,partition information,boot block,super block,
     * free space management,i-node,root directory,files and directories
     */
    public void initialize() {

    }

    public long createFile(String name) {
        // 在i-node和file name映射关系中查找文件是否存在
        long inodeNumber = fileNameInodeManager.getInodeNumber(name);
        // 根据i-node number查找i-node是否删除
        if (inodeNumber != -1L && !inodeManager.isInodeDeleted(inodeNumber)) {
            // 文件已存在
            return -2L;
        }

        // 查找i-node 位图是否有空的i-node节点位置
        int index = inodeBitMapManager.getFreeInodeIndex();
        if (index == -1) {
            return -3L;
        }

        // 创建i-node节点
        inodeNumber = inodeManager.createInode(index);
        if (inodeNumber == -1L) {
            return -4L;
        }

        // 创建i-node和filename的映射关系;
        // 在i-node和filename的映射区新建关系
        // index * (file&i-node).maxLength为数据区偏移起始点
        fileNameInodeManager.createFileInode(index, inodeNumber, name);


        // 更新i-node位图为已使用
        inodeBitMapManager.setBlockUsed(index);

        // 更新super block inode统计
        superBlockManager.incrementInodeAmount();

        return inodeNumber;
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
