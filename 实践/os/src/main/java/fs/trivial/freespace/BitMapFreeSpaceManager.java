package fs.trivial.freespace;

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

    public BitMapFreeSpaceManager(final CaSystem caSystem) {
        this.caSystem = caSystem;

    }

    @Override
    public boolean initialize() {
        long offset = caSystem.getSuperBlockManager().getFreeSpaceStartPage() * caSystem.getBlockSize();
        long readOffset = offset;
        if (caSystem.getBootBlockManager().getIsInit()) {
            // 从空闲区管理块读取数据
            byte[] freeSpaceData = caSystem.getDiskHelper().read(readOffset, (int) (caSystem.getBlockSize() * caSystem.getSuperBlockManager().getFreeSpacePages()));
            // 写入bitmap
            freeSpaceBitMap = new BitMap(freeSpaceData);
        } else {
            this.freeSpaceBitMap = new BitMap((int) caSystem.getSuperBlockManager().getFreeSpacePages());
            // 写入空闲区
            caSystem.getDiskHelper().write(freeSpaceBitMap.toByteArray(), offset);
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
}
