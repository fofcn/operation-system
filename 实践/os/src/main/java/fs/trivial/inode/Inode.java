package fs.trivial.inode;

import helper.annotation.SerializerOrder;

/**
 * i-node
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class Inode {
    /**
     * i-node index number
     */
    @SerializerOrder(1)
    private long number = 0;

    /**
     * create time
     */
    @SerializerOrder(2)
    private long createTime = System.currentTimeMillis();

    /**
     * last access time
     */
    @SerializerOrder(3)
    private long lastAccessTime = System.currentTimeMillis();

    /**
     * last modified time
     */
    @SerializerOrder(4)
    private long lastModifiedTime = System.currentTimeMillis();

    /**
     * first block number
     */
    @SerializerOrder(5)
    private long firstBlockNumber = 0;

    /**
     * length of file
     */
    @SerializerOrder(6)
    private long length = 0;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getFirstBlockNumber() {
        return firstBlockNumber;
    }

    public void setFirstBlockNumber(long firstBlockNumber) {
        this.firstBlockNumber = firstBlockNumber;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
