package fs.trivial;

import fs.helper.DiskHelper;
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
    private static final int MAX_PATH = 255;

    private static final int FS_MAGIC_NUMBER = 0x12345678;

    private final Partition partition;

    private SuperBlock superBlock;

    private final int blockSize;

    private BootBlock bootBlock;

    private DiskHelper diskHelper;

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
            diskHelper = new DiskHelper("test___", 1024 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize the file system according to the amount of partition space.
        long size = partition.getEnd() - partition.getStart() + 1;

        // Initialize the boot block of the file system
        // 引导块占用一页
        bootBlock = new BootBlock();
        long bootPage = 1;

        // Initialize the super block of the file system.
        // 超级块占用一页
        superBlock = new SuperBlock();
        superBlock.setMagic(FS_MAGIC_NUMBER);
        // the amount of blocks.
        long blockAmount = size / blockSize;
        superBlock.setBlockAmount(blockAmount);
        long superBlockPage = 1;

        // Initialize the free space management of the file system.
        // 空闲区管理占用n页
        FreeSpaceManager freeSpaceManager = new BitMapFreeSpaceManager(blockAmount);
        long freeSpacePages = blockAmount / 8 / blockSize;

        // Initialize the i-node space of the file system.
        INode iNode = new INode();
        byte[] iNodeBytes = ByteArraySerializer.serialize(iNode, INode.class);
        long iNodeSize = blockAmount * iNodeBytes.length;
        // i-node占用n页
        long inodePages = iNodeSize / blockSize;
        // Initialize the root directory of the file system
        RootDirectory rootDirectory = new RootDirectory();
        long rootPages = (blockAmount * 8 + 10) / blockAmount;

        // Initialize the association of file name and inode number.
        // inode index number has 64 bits, and file name has MAX_PATH*8 bits
        // 文件名和i-node管理占用n页
        long fileNameInodePages = ((8 + 256) * blockAmount) / blockSize;

        long dataStartPage = bootPage + superBlockPage + freeSpacePages + inodePages + rootPages + fileNameInodePages;
        superBlock.setDataStartBlockNumber(dataStartPage);
    }


}
