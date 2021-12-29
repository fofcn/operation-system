package fs.trivial.superblock;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import lang.serializer.ByteArraySerializer;

/**
 * super block manager
 *
 * @author jiquanxi
 * @date 2021/12/29
 */
public class SuperBlockManager implements Manager {

    private static final int FS_MAGIC_NUMBER = 0x12345678;

    private static final int MAX_PATH = 255;

    private SuperBlock superBlock;

    private final CaSystem caSystem;

    public SuperBlockManager(final CaSystem caSystem) {
        this.caSystem = caSystem;
    }

    @Override
    public boolean initialize() {
        if (!caSystem.getBootBlockManager().getIsInit()) {
            // Initialize the super block of the file system.
            // 超级块占用一页
            superBlock = new SuperBlock();
            superBlock.setMagic(FS_MAGIC_NUMBER);
            // the amount of blocks.
            long blockAmount = caSystem.getPartitionSize() / caSystem.getBlockSize();
            superBlock.setBlockAmount(blockAmount);

            // 计算i-node数量，每个块一个inode
            int iNodeSize = caSystem.getInodeManager().getIndexNodeSize();
            long inodeAmount = blockAmount * iNodeSize;
            // i-node占用n页
            long inodePages = inodeAmount / caSystem.getBlockSize() + iNodeSize % caSystem.getBlockSize() == 0 ? 0 : 1;
            superBlock.setInodeAmount(inodePages);
            superBlock.setInodeStartPage(2);

            // 计算free space空间
            long freeSpacePages = blockAmount / (8 * caSystem.getBlockSize());
            superBlock.setFreeSpacePages(freeSpacePages);
            superBlock.setFreeSpaceStartPage(2 + inodePages);

            // 计算root directory空间
            long rootDirectoryPages = (blockAmount * (1 + 8)) / caSystem.getBlockSize();
            superBlock.setRootDirectoryPages(rootDirectoryPages);
            superBlock.setRootDirectoryStartPage(2 + inodePages + freeSpacePages);

            // 计算file name i-node空间
            long fileInodePages = (blockAmount * (MAX_PATH + 8 + 4)) / caSystem.getBlockSize();
            superBlock.setFileInodePages(fileInodePages);
            superBlock.setFileInodeStartPage(2 + inodePages + freeSpacePages + fileInodePages);

            // 计算数据起始块号
            superBlock.setDataStartPage(2 + inodePages + freeSpacePages + fileInodePages + fileInodePages);

            // 写入硬盘
            byte[] superBlockBytes = ByteArraySerializer.serialize(superBlock, SuperBlock.class);
            caSystem.getDiskHelper().write(superBlockBytes, 1 * caSystem.getBlockSize());
        }

        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
