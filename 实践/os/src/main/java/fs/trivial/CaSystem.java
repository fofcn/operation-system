package fs.trivial;

import fs.helper.DiskHelper;
import fs.trivial.boot.BootBlock;
import fs.trivial.boot.BootBlockManager;
import fs.trivial.freespace.BitMapFreeSpaceManager;
import fs.trivial.freespace.FreeSpaceManager;
import fs.trivial.inode.INode;
import fs.trivial.inode.InodeManager;
import fs.trivial.inodetable.FileNameInodeManager;
import fs.trivial.root.RootDirectory;
import fs.trivial.root.RootDirectoryManager;
import fs.trivial.superblock.SuperBlock;
import fs.trivial.superblock.SuperBlockManager;
import lang.serializer.ByteArraySerializer;

import java.io.IOException;

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
 * -----------------------------------------------------------------------------------------------------
 * | boot block | super block | free space management | i-node | root directory | files and directories|
 * -----------------------------------------------------------------------------------------------------
 * @author jiquanxi
 * @date 2021/12/27
 */
public class CaSystem {


    private final Partition partition;

    private final int blockSize;

    private FreeSpaceManager freeSpaceManager;

    private DiskHelper diskHelper;

    private BootBlockManager bootBckManager;

    private SuperBlockManager superBlockManager;

    private BitMapFreeSpaceManager freeSpaceManager;

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
        // Initialize a disk.
        try {
            diskHelper = new DiskHelper("test___", partition.getEnd() - partition.getStart());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize the file system according to the amount of partition space.

        // Initialize the free space management of the file system.
        // 空闲区管理占用n页
        FreeSpaceManager freeSpaceManager = new BitMapFreeSpaceManager(blockAmount);
        long freeSpacePages = blockAmount / (8 * blockSize) + blockAmount % (8 * blockSize) == 0 ? 0 : 1;

        // Initialize the i-node space of the file system.
        INode iNode = new INode();
        byte[] iNodeBytes = ByteArraySerializer.serialize(iNode, INode.class);
        long iNodeSize = blockAmount * iNodeBytes.length;
        // i-node占用n页
        long inodePages = iNodeSize / blockSize + iNodeSize % blockSize == 0 ? 0 : 1;
        // Initialize the root directory of the file system
        rootDirectory = new RootDirectory();
        rootDirectory.setName("/");
        long rootPages = (blockAmount * 8 + 10) / blockAmount + (blockAmount * 8 + 10) % blockAmount == 0 ? 0 : 1;

        // Initialize the association of file name and inode number.
        // inode index number has 64 bits, and file name has MAX_PATH*8 bits
        // 文件名和i-node管理占用n页
        long fileNameInodePages = ((8 + 256) * blockAmount) / blockSize + ((8 + 256) * blockAmount) % blockSize == 0 ? 0 : 1;

        long dataStartPage = bootPage + superBlockPage + freeSpacePages + inodePages + rootPages + fileNameInodePages;
        superBlock.setDataStartBlockNumber(dataStartPage);

        initializeMetaInfo(freeSpacePages, inodePages);
    }

    private boolean checkIfInitialized() {
        // read boot block
        byte[] bootBlockBytes = diskHelper.read(0, blockSize);
        if (bootBlockBytes == null) {
            // todo
        }

        bootBlock = ByteArraySerializer.deserialize(BootBlock.class, bootBlockBytes);
        if (bootBlock.getIsInit() == 1) {
            return true;
        }

        return false;
        // read super block

        // read free space

        // read i-node(skip)

        // read root directory

        // read files and directories (skip)
    }

    private void initializeMetaInfo(long freeSpacePages, long inodePages) {
        byte[] bootBytes = ByteArraySerializer.serialize(bootBlock, BootBlock.class);
        diskHelper.write(bootBytes, 0);

        byte[] superBlockBytes = ByteArraySerializer.serialize(superBlock, SuperBlock.class);
        diskHelper.write(superBlockBytes, blockSize);


        // byte[] freeSpaceBytes =

        // i-node

        // root
        long offset = blockSize * (1 + 1 + freeSpacePages + inodePages);
        byte[] rootBlockBytes = ByteArraySerializer.serialize(rootDirectory, RootDirectory.class);
        diskHelper.write(rootBlockBytes, offset);
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
}
