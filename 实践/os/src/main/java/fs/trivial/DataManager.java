package fs.trivial;

import fs.helper.DiskHelper;
import fs.trivial.inode.Inode;
import fs.trivial.superblock.SuperBlockManager;

/**
 * 数据管理
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class DataManager implements Manager {

    private final DiskHelper diskHelper;

    private final long blockSize;

    private long startOffset;

    private long dataStartPage;

    private final SuperBlockManager superBlockManager;

    public DataManager(CaSystem caSystem) {
        this.blockSize = caSystem.getBlockSize();
        this.diskHelper = caSystem.getDiskHelper();
        this.superBlockManager = caSystem.getSuperBlockManager();
    }

    @Override
    public boolean initialize() {
        this.dataStartPage = superBlockManager.getDataStartPage();
        this.startOffset = superBlockManager.getDataStartPage() * blockSize;
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public void writeData(byte[] content, int start, int end) {
        long writeStartOffset = startOffset + start * blockSize;
        diskHelper.write(content, (int) writeStartOffset, content.length);
    }

    public byte[] readData(Inode inode) {
        long readStartOffset = startOffset + inode.getFirstBlockNumber() * blockSize;
        return diskHelper.read(readStartOffset, (int) inode.getLength());
    }
}
