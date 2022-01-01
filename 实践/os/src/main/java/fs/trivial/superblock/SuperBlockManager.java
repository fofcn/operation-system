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

            // inode amount
            superBlock.setInodeAmount(0L);

            // 计算root directory空间
            long rootDirectoryPages = 1;
            superBlock.setRootDirectoryPages(rootDirectoryPages);
            superBlock.setRootDirectoryStartPage(2);

            // 计算i-node bit map的块
            long inodeBitMapPages = (blockAmount / (8 * caSystem.getBlockSize()));
            superBlock.setInodeBitMapStartPage(3);
            superBlock.setInodeBitMapPages(inodeBitMapPages);

            // 计算i-node数量，每个块一个inode
            int iNodeSize = caSystem.getInodeManager().getIndexNodeSize();
            long inodeAmount = blockAmount * iNodeSize;
            // i-node占用n页
            long inodePages = inodeAmount / caSystem.getBlockSize() + iNodeSize % caSystem.getBlockSize() == 0 ? 0 : 1;
            superBlock.setInodePages(inodePages);
            superBlock.setInodeStartPage(3 + inodeBitMapPages);

            // 计算free space空间
            long freeSpacePages = blockAmount / (8 * caSystem.getBlockSize());
            superBlock.setFreeSpacePages(freeSpacePages);
            superBlock.setFreeSpaceStartPage(3 + inodeBitMapPages + inodePages);

            // 计算file name i-node位图空间
            long fileInodeBitMapPages = blockAmount / (8 * caSystem.getBlockSize());
            long fileInodeBitMapStartPage = 3 + inodeBitMapPages + inodePages + freeSpacePages;
            superBlock.setFileInodeBitMapPages(fileInodeBitMapPages);
            superBlock.setFileInodeBitMapStartPage(fileInodeBitMapStartPage);

            // 计算file name i-node空间
            long fileInodePages = (blockAmount * (MAX_PATH + 8 + 4)) / caSystem.getBlockSize();
            superBlock.setFileInodePages(fileInodePages);
            superBlock.setFileInodeStartPage(3 + inodeBitMapPages + inodePages + freeSpacePages + fileInodeBitMapPages + fileInodePages);

            // 计算数据起始块号
            superBlock.setDataStartPage(3 + inodeBitMapPages + inodePages + freeSpacePages + fileInodePages + fileInodeBitMapPages + fileInodePages);

            // 写入硬盘
            byte[] superBlockBytes = ByteArraySerializer.serialize(superBlock, SuperBlock.class);
            caSystem.getDiskHelper().write(superBlockBytes, caSystem.getBlockSize());
        }

        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public long getFreeSpacePages() {
        return superBlock.getFreeSpacePages();
    }

    public long getFreeSpaceStartPage() {
        return superBlock.getFreeSpaceStartPage();
    }

    public long getRootDirectoryStartPage() {
        return superBlock.getRootDirectoryStartPage();
    }

    public long getRootDirectoryPages() {
        return superBlock.getRootDirectoryPages();
    }

    public long getInodeBitMapStartPage() {
        return superBlock.getInodeBitMapStartPage();
    }

    public long getInodeBitMapPages() {
        return superBlock.getInodeBitMapPages();
    }

    public long getFileInodeStartPage() {
        return superBlock.getFileInodeStartPage();
    }

    public long getFileInodePages() {
        return superBlock.getFileInodePages();
    }

    public long getInodeAmount() {
        return superBlock.getInodeAmount();
    }

    public void incrementInodeAmount() {
        superBlock.setInodeAmount(superBlock.getInodeAmount() + 1);
        // todo 记得处理磁盘持久化和互斥问题
        // todo s互斥就原子对象就可以了 AtomicLong
    }

    public long getInodeStartPage() {
        return superBlock.getInodeStartPage();
    }

    public long getInodePages() {
        return superBlock.getInodePages();
    }
}
