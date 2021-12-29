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
 * ----------------------------------------------------------------------------------------------------------------------
 * | boot block | super block | free space management | i-node bit map | i-node | root directory | files and directories|
 * ---------------------------------------------------------------------------------------------------------------------
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
