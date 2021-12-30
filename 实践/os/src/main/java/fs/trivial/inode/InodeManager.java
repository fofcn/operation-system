package fs.trivial.inode;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import lang.serializer.ByteArraySerializer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * I-node manager of file system.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class InodeManager implements Manager {

    private AtomicLong inodeNumber = new AtomicLong(1L);

    private final CaSystem caSystem;

    private final long dataStartOffset;

    private final long dataEndOffset;

    public InodeManager(CaSystem caSystem) {
        this.caSystem = caSystem;
        this.dataStartOffset = caSystem.getSuperBlockManager().getInodeStartPage() * caSystem.getBlockSize();
        this.dataEndOffset = dataStartOffset + caSystem.getSuperBlockManager().getInodePages() * caSystem.getBlockSize();
    }

    @Override
    public boolean initialize() {
        // 根据super block中的i-node数量加载i-node
        return false;
    }


    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public long createInode(int index) {
        Inode inode = new Inode();
        inode.setNumber(inodeNumber.incrementAndGet());
        // 通过位图索引定位位置
        // todo i-node固定长度获取
        int inodeByteLength = 0;
        long writeOffset = index * inodeByteLength + dataStartOffset;
        byte[] inodeBytes = ByteArraySerializer.serialize(inode, Inode.class);
        caSystem.getDiskHelper().write(inodeBytes, writeOffset);
        return 0L;
    }

    public int getIndexNodeSize() {
        Inode iNode = new Inode();
        byte[] iNodeBytes = ByteArraySerializer.serialize(iNode, Inode.class);
        return iNodeBytes.length;
    }

    public boolean isInodeDeleted(long inodeNumber) {
        return false;
    }
}
