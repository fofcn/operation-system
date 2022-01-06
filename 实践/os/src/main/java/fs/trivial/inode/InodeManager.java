package fs.trivial.inode;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import helper.annotation.FixedByteSerializer;
import lang.serializer.ByteArraySerializer;

/**
 * I-node manager of file system.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class InodeManager implements Manager {

    private final CaSystem caSystem;

    private long dataStartOffset;

    private long dataEndOffset;

    private final int inodeSize;

    private final InodeCache inodeCache;

    public InodeManager(CaSystem caSystem) {
        this.caSystem = caSystem;
        this.inodeSize = getIndexNodeSize();
        this.inodeCache = new InodeCache();
    }

    @Override
    public boolean initialize() {
        this.dataStartOffset = caSystem.getSuperBlockManager().getInodeStartPage() * caSystem.getBlockSize();
        this.dataEndOffset = dataStartOffset + caSystem.getSuperBlockManager().getInodePages() * caSystem.getBlockSize();
        this.inodeCache.initialize();
        // 根据super block中的i-node数量加载i-node
        return true;
    }


    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public long createInode(int index) {
        Inode inode = new Inode();
        inode.setNumber(index);
        // 通过位图索引定位位置

        flushInode(inode);

        // 将inode加入到缓存中
        inodeCache.set(inode);
        return index;
    }

    public int getIndexNodeSize() {
        Inode iNode = new Inode();
        byte[] iNodeBytes = ByteArraySerializer.serialize(iNode, Inode.class);
        return iNodeBytes.length;
    }

    public boolean isInodeDeleted(long inodeNumber) {
        Inode inode = getInodeInternal(inodeNumber);
        if (inode != null) {
            return inode.getIsDeleted() == 1;
        }
        // 找到就将i-node加载到hash缓存中
        // 无法找到就返回false：todo 更精细点做就是返回一个错误对象，里面写明错误码和错误信息
        return false;
    }

    public Inode getInode(long inodeNumber) {
        return getInodeInternal(inodeNumber);
    }

    private Inode getInodeInternal(long inodeNumber) {
        // 先在inode缓存中查找
        Inode inode = inodeCache.get(inodeNumber);
        if (inode != null) {
            return inode;
        }

        // 通过i-node number查找i-node
        for (long i = dataStartOffset, j = 0; i < dataEndOffset && j < caSystem.getSuperBlockManager().getInodeAmount(); i = i + inodeSize, j++) {
            byte[] inodeBytes = caSystem.getDiskHelper().read(i, inodeSize);
            inode = ByteArraySerializer.deserialize(Inode.class, inodeBytes);
            if (inode == null) {
                return null;
            }

            if (inode.getNumber() == inodeNumber) {
                inodeCache.set(inode);
                return inode;
            }
        }

        return null;
    }

    public void updateOnWrite(Inode inode, int length, int start) {
        inode.setLength(inode.getLength() + length);
        inode.setFirstBlockNumber(start);
        inodeCache.set(inode);
        flushInode(inode);
    }

    private void flushInode(Inode inode) {
        int inodeByteLength = FixedByteSerializer.getSerializeLength(Inode.class);
        long writeOffset = inode.getNumber() * inodeByteLength + dataStartOffset;
        byte[] inodeBytes = ByteArraySerializer.serialize(inode, Inode.class);
        caSystem.getDiskHelper().write(inodeBytes, writeOffset);
    }
}
