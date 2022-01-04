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

    private volatile long startOffset;

    private volatile long endOffset;

    public InodeBitMapManager(CaSystem caSystem) {
        this.caSystem = caSystem;

    }

    @Override
    public boolean initialize() {
        this.startOffset = caSystem.getSuperBlockManager().getInodeBitMapStartPage() * caSystem.getBlockSize();
        this.endOffset = startOffset + caSystem.getSuperBlockManager().getInodeBitMapPages() * caSystem.getBlockSize();
        long inodeBitMapPages = caSystem.getSuperBlockManager().getInodeBitMapPages();
        if (caSystem.getBootBlockManager().getIsInit()) {
            // 从空闲区管理块读取数据
            byte[] freeSpaceData = caSystem.getDiskHelper().read(startOffset, (int) (caSystem.getBlockSize() * inodeBitMapPages));
            // 写入bitmap
            bitMap = new BitMap(freeSpaceData);
        } else {
            this.bitMap = new BitMap((int) inodeBitMapPages * caSystem.getBlockSize());
            this.bitMap.clearAll();
            // 写入空闲区
            caSystem.getDiskHelper().write(bitMap.toByteArray(), startOffset);
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
        //
        // todo 记得处理持久化到磁盘
        // todo 原子操作
        byte[] bytes = bitMap.getBytes(index);
        // todo 这里暴露了实现，可以优化
        caSystem.getDiskHelper().write(bytes, startOffset + index / 32);
    }
}
