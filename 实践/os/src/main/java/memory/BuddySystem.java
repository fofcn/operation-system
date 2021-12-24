package memory;

import lang.LinkedList;
import util.StdOut;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 伙伴系统
 *
 * @author jiquanxi
 * @date 2021/12/20
 */
public class BuddySystem {
    /**
     * 申请释放锁
     */
    private Lock lock = new ReentrantLock();
    private LinkedList<MemoryBlock> mmBlockList = new LinkedList<>();
    /**
     * 可用空闲区全部大小
     */
    private final int size;
    /**
     * 空闲区可用大小
     */
    private AtomicInteger idleSize = new AtomicInteger(0);

    /**
     * 内存块数量
     */
    private AtomicInteger blockSize = new AtomicInteger(0);

    /**
     * 是否初始化完成
     */
    private volatile boolean isInitialized = false;

    public BuddySystem(int size) {
        this.size = size;
    }

    /**
     * 初始化伙伴系统内存链表
     */
    public void initialize() {
        if (isInitialized) {
            return;
        }
        MemoryBlock memoryBlock = new MemoryBlock();
        memoryBlock.setSize(size);
        memoryBlock.setStart(0);
        memoryBlock.setEnd(size - 1);
        mmBlockList.add(memoryBlock);
        blockSize.incrementAndGet();
        isInitialized = true;
    }

    /**
     * 分配内存
     * @param expectSize 待分配的内存大小
     */
    public MemoryBlock alloc(int expectSize) {
        if (expectSize > size) {
            throw new IllegalArgumentException("Sorry, I don't have enough memory.");
        }
        MemoryBlock best = null;

        try {
            lock.lock();
            // 查看当前空闲块链表，找到小且大于等于申请大小的空闲块
            MemoryBlock smallestFit = findSmallestFitBlock(expectSize);
            if (smallestFit == null) {
                throw new InternalError("not enough memory");
            }
            // 检查是否满足2^u-1 < expectSize <= 2^u
            // 满足则直接分配
            // 不满足则拆分为两个大于或等于S的小块，然后检查是否满足2^u-1 < size <= 2^u
            if (smallestFit.getSize() != expectSize) {
                // 检查并等分内存区
                while (expectSize * 2 <= smallestFit.getSize()) {
                    // 拆分
                    int smallBlockSize = smallestFit.getSize() / 2;
                    int start = smallestFit.getStart();
                    int middle = smallestFit.getStart() + smallBlockSize;
                    int end = smallestFit.getEnd();
                    MemoryBlock left = new MemoryBlock(start, middle, smallBlockSize, false);
                    MemoryBlock right = new MemoryBlock(middle, end, smallBlockSize, false);
                    // 在删除点添加这两块内存
                    mmBlockList.replace(smallestFit, left, right);
                    blockSize.incrementAndGet();
                    smallestFit = left;
                }
            }

            best = smallestFit;
            best.setUsed(true);
            idleSize.addAndGet(-best.getSize());
        } finally {
            lock.unlock();
        }

        return best;
    }

    private MemoryBlock findSmallestFitBlock(int expectSize) {
        MemoryBlock smallestFit = null;
        for (MemoryBlock memoryBlock : mmBlockList) {
            if (!memoryBlock.isUsed() &&
                    memoryBlock.getSize() >= expectSize) {
                if (smallestFit == null) {
                    smallestFit = memoryBlock;
                } else {
                    if (smallestFit.getSize() > memoryBlock.getSize()) {
                        smallestFit = memoryBlock;
                    }
                }
            }
        }
        return smallestFit;
    }

    public void free(MemoryBlock freeBlock) {
        List<LinkedList.Node<MemoryBlock>> mergeNodes = new ArrayList<>(3);
        // 释放内存需要检查四种相邻场景
        // 如果有相邻，则合并
        try {
            freeBlock.setUsed(false);
            lock.lock();
            LinkedList.Node<MemoryBlock> toFreeNode = mmBlockList.findNode(freeBlock);
            if (toFreeNode == null) {
                throw new IllegalArgumentException("You accessed memory illegally.");
            }

            // 检查前一个节点
            // 检查查找到的块是否大小相等且地址连续
            LinkedList.Node<MemoryBlock> prev = toFreeNode.prev;
            LinkedList.Node<MemoryBlock> next = toFreeNode.next;
            if (prev != null && !prev.item.isUsed()
                    && prev.item.getSize() == toFreeNode.item.getSize()
                    && prev.item.getEnd() == toFreeNode.item.getStart()) {
                mergeNodes.add(prev);
                blockSize.incrementAndGet();
            }

            mergeNodes.add(toFreeNode);

            // 检查下一个节点
            // 检查查找到的块是否大小相等且地址连续
            if (next != null && !next.item.isUsed()
                    && next.item.getSize() == toFreeNode.item.getSize()
                    && next.item.getStart() == toFreeNode.item.getEnd()) {
                mergeNodes.add(next);
            }

            int start = mergeNodes.get(0).item.getStart();
            int end = mergeNodes.get(mergeNodes.size() - 1).item.getEnd();
            int size = 0;

            for (LinkedList.Node<MemoryBlock> node : mergeNodes) {
                size += node.item.getSize();
                blockSize.decrementAndGet();
            }


            // 删除当前节点和下一个节点，然后更新第一个节点大小和连接
            mmBlockList.remove(prev);
            mmBlockList.remove(next);
            toFreeNode.item.setStart(start);
            toFreeNode.item.setEnd(end);
            toFreeNode.item.setSize(size);
        } finally {
            lock.unlock();
        }
    }

    public void printBlocks() {
        int index = 1;
        for (MemoryBlock memoryBlock : mmBlockList) {
            StdOut.println("index: " + index + " " + memoryBlock);
            index++;
        }
    }

    public int getSize() {
        return size;
    }

    public int getIdleSize() {
        return idleSize.get();
    }

    public int getBlockSize() {
        return blockSize.get();
    }
}
