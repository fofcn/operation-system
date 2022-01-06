package fs.trivial;

import fs.helper.DiskHelper;

/**
 * 数据管理
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class DataManager implements Manager {

    private final DiskHelper diskHelper;

    private final long startOffset;

    private final long blockSize;

    private final long dataStartPage;

    public DataManager(CaSystem caSystem) {
        this.blockSize = caSystem.getBlockSize();
        this.diskHelper = caSystem.getDiskHelper();
        this.dataStartPage = caSystem.getSuperBlockManager().getDataStartPage();
        this.startOffset = caSystem.getSuperBlockManager().getDataStartPage() * blockSize;
    }

    @Override
    public boolean initialize() {
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
        diskHelper.write(content, (int) writeStartOffset, end - start);
    }
}
