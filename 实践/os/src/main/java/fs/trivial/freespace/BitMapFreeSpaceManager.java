package fs.trivial.freespace;

import fs.helper.DiskHelper;
import fs.trivial.CaSystem;
import fs.trivial.Manager;
import lang.BitMap;

/**
 * a bit map structure for free space management.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class BitMapFreeSpaceManager implements FreeSpaceManager, Manager {

    private BitMap freeSpaceBitMap;

    private final CaSystem caSystem;

    private long startOffset;

    private long endOffset;

    private final long blockSize;

    private final DiskHelper diskHelper;

    public BitMapFreeSpaceManager(final CaSystem caSystem) {
        this.caSystem = caSystem;
        this.diskHelper = caSystem.getDiskHelper();
        this.blockSize = caSystem.getBlockSize();

    }

    @Override
    public boolean initialize() {
        this.startOffset = caSystem.getSuperBlockManager().getFreeSpaceStartPage() * caSystem.getBlockSize();
        this.endOffset = startOffset + caSystem.getBlockSize() * caSystem.getSuperBlockManager().getFreeSpacePages();
        // 这里一次性加载完是有性能和资源问题的
        // 因为空闲区可能会比较大，完全加载到内存可能导致速度慢
        // 可能导致内存资源不够，尤其是在硬盘容量特别大的时候
        if (caSystem.getBootBlockManager().getIsInit()) {
            // 从空闲区管理块读取数据
            byte[] freeSpaceData = diskHelper.read(startOffset, (int) (blockSize * caSystem.getSuperBlockManager().getFreeSpacePages()));
            // 写入bitmap
            freeSpaceBitMap = new BitMap(freeSpaceData);
        } else {
            this.freeSpaceBitMap = new BitMap((int) caSystem.getSuperBlockManager().getFreeSpacePages());
            // 写入空闲区
            diskHelper.write(freeSpaceBitMap.toByteArray(), startOffset);
        }

        return true;
    }

    @Override
    public void start() {
        // 周期性回写磁盘或满足更新次数计数阈值回写磁盘
    }

    @Override
    public void shutdown() {

    }

    public GetFreeSpaceIndexes getFreeIndexes(int blocks) {
        if (blocks <= 0) {
            return null;
        }

        int expectSize = blocks;
        int startIndex = -1;
        int endIndex = -1;

        // 读取一块到bitmap中，然后遍历查询是否有连续空的块
        boolean found = false;
        int index = 0;
        long tmpStartOffset = startOffset;
        while (tmpStartOffset <= endOffset) {
            byte[] pageBytes = diskHelper.read(tmpStartOffset, (int) blockSize);
            BitMap bitMap = new BitMap(pageBytes);
            for (int i = 0; i < bitMap.size(); i++, index++) {
                if (!bitMap.checkBit(i)) {
                    if (startIndex == -1) {
                        startIndex = index;
                    }

                    expectSize--;
                    if (expectSize == 0) {
                        endIndex = index;
                        found = true;
                        break;
                    }
                } else {
                    expectSize = blocks;
                }
            }

            if (found) {
                break;
            }
            tmpStartOffset += blockSize;
        }

        GetFreeSpaceIndexes result = new GetFreeSpaceIndexes();
        result.setStart(startIndex);
        result.setEnd(endIndex == 0 ? 1 : endIndex);

        return found ? result : null;
    }

    public void setBlockUsed(int start, int end) {
        BitMap bitMap = new BitMap(end - start);
        bitMap.setAll();
        byte[] usedBytes = bitMap.toByteArray();

        diskHelper.write(usedBytes, startOffset + start);
    }
}
