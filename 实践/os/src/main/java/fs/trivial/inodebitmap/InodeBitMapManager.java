package fs.trivial.inodebitmap;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import lang.BitMap;

/**
 * i-node bit map manager
 *
 * @author jiquanxi
 * @date 2021/12/29
 */
public class InodeBitMapManager implements Manager {

    private final CaSystem caSystem;

    private BitMap bitMap;

    public InodeBitMapManager(CaSystem caSystem) {
        this.caSystem = caSystem;
    }

    @Override
    public boolean initialize() {
        long offset = caSystem.getSuperBlockManager().getInodeBitMapStartPage() * caSystem.getBlockSize();
        long inodeBitMapPages = caSystem.getSuperBlockManager().getInodeBitMapPages();
        long readOffset = offset;
        if (caSystem.getBootBlockManager().getIsInit()) {
            // 从空闲区管理块读取数据
            byte[] freeSpaceData = caSystem.getDiskHelper().read(readOffset, (int) (caSystem.getBlockSize() * inodeBitMapPages));
            // 写入bitmap
            bitMap = new BitMap(freeSpaceData);
        } else {
            this.bitMap = new BitMap((int) inodeBitMapPages * caSystem.getBlockSize());
            // 写入空闲区
            caSystem.getDiskHelper().write(bitMap.toByteArray(), offset);
        }

        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public int getFreeInodeIndex() {
        for (int i = 0; i < bitMap.size(); i++) {
            boolean used = bitMap.checkBit(i);
            if (!used) {
                return i;
            }
        }

        return -1;
    }

    public void setBlockUsed(int index) {
        bitMap.setBit(index);

        // todo 记得处理持久化到磁盘
    }
}
