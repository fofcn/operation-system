package com.fofcn.mm.buddy;

/**
 * 内存块
 *
 * @author jiquanxi
 * @date 2021/12/20
 */
public class MemoryBlock {
    /**
     * 起始位置
     */
    private int start;

    /**
     * 结束位置
     */
    private int end;

    /**
     * 内存大小
     */
    private int size;

    /**
     * 是否已经使用
     */
    private boolean isUsed;

    public MemoryBlock() {
    }

    public MemoryBlock(int start, int end, int size, boolean isUsed) {
        this.start = start;
        this.end = end;
        this.size = size;
        this.isUsed = isUsed;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    @Override
    public String toString() {
        return "MemoryBlock{" +
                "start=" + start +
                ", end=" + end +
                ", size=" + size +
                ", isUsed=" + isUsed +
                '}';
    }
}
