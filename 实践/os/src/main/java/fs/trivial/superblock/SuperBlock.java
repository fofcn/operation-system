package fs.trivial.superblock;

import helper.annotation.FixedByteSerializer;
import helper.annotation.SerializerOrder;

/**
 * super block 超级块
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
public class SuperBlock implements FixedByteSerializer {

    @SerializerOrder(1)
    private int magic;

    /**
     * the amount of blocks.
     */
    @SerializerOrder(2)
    private long blockAmount;

    /**
     * i-node amount
     */
    @SerializerOrder(3)
    private long inodeAmount;

    /**
     * the start block number for data storing.
     */
    @SerializerOrder(4)
    private long freeSpacePages;

    @SerializerOrder(5)
    private long freeSpaceStartPage;

    @SerializerOrder(6)
    private long inodePages;

    @SerializerOrder(7)
    private long inodeStartPage;

    @SerializerOrder(8)
    private long rootDirectoryPages;

    @SerializerOrder(9)
    private long rootDirectoryStartPage;

    @SerializerOrder(10)
    private long fileInodePages;

    @SerializerOrder(11)
    private long fileInodeStartPage;

    @SerializerOrder(12)
    private long dataStartPage;

    @SerializerOrder(13)
    private long inodeBitMapPages;

    @SerializerOrder(14)
    private long inodeBitMapStartPage;

    @SerializerOrder(15)
    private long fileInodeBitMapPages;

    @SerializerOrder(16)
    private long fileInodeBitMapStartPage;

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public long getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(long blockAmount) {
        this.blockAmount = blockAmount;
    }

    public long getInodeAmount() {
        return inodeAmount;
    }

    public void setInodeAmount(long inodeAmount) {
        this.inodeAmount = inodeAmount;
    }

    public long getFreeSpacePages() {
        return freeSpacePages;
    }

    public void setFreeSpacePages(long freeSpacePages) {
        this.freeSpacePages = freeSpacePages;
    }

    public long getFreeSpaceStartPage() {
        return freeSpaceStartPage;
    }

    public void setFreeSpaceStartPage(long freeSpaceStartPage) {
        this.freeSpaceStartPage = freeSpaceStartPage;
    }

    public long getInodePages() {
        return inodePages;
    }

    public void setInodePages(long inodePages) {
        this.inodePages = inodePages;
    }

    public long getInodeStartPage() {
        return inodeStartPage;
    }

    public void setInodeStartPage(long inodeStartPage) {
        this.inodeStartPage = inodeStartPage;
    }

    public long getRootDirectoryPages() {
        return rootDirectoryPages;
    }

    public void setRootDirectoryPages(long rootDirectoryPages) {
        this.rootDirectoryPages = rootDirectoryPages;
    }

    public long getRootDirectoryStartPage() {
        return rootDirectoryStartPage;
    }

    public void setRootDirectoryStartPage(long rootDirectoryStartPage) {
        this.rootDirectoryStartPage = rootDirectoryStartPage;
    }

    public long getFileInodePages() {
        return fileInodePages;
    }

    public void setFileInodePages(long fileInodePages) {
        this.fileInodePages = fileInodePages;
    }

    public long getFileInodeStartPage() {
        return fileInodeStartPage;
    }

    public void setFileInodeStartPage(long fileInodeStartPage) {
        this.fileInodeStartPage = fileInodeStartPage;
    }

    public long getDataStartPage() {
        return dataStartPage;
    }

    public void setDataStartPage(long dataStartPage) {
        this.dataStartPage = dataStartPage;
    }

    public long getInodeBitMapPages() {
        return inodeBitMapPages;
    }

    public void setInodeBitMapPages(long inodeBitMapPages) {
        this.inodeBitMapPages = inodeBitMapPages;
    }

    public long getInodeBitMapStartPage() {
        return inodeBitMapStartPage;
    }

    public void setInodeBitMapStartPage(long inodeBitMapStartPage) {
        this.inodeBitMapStartPage = inodeBitMapStartPage;
    }

    public long getFileInodeBitMapPages() {
        return fileInodeBitMapPages;
    }

    public void setFileInodeBitMapPages(long fileInodeBitMapPages) {
        this.fileInodeBitMapPages = fileInodeBitMapPages;
    }

    public long getFileInodeBitMapStartPage() {
        return fileInodeBitMapStartPage;
    }

    public void setFileInodeBitMapStartPage(long fileInodeBitMapStartPage) {
        this.fileInodeBitMapStartPage = fileInodeBitMapStartPage;
    }
}
