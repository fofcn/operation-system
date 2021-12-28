package fs.trivial;

/**
 * i-node
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class INode {
    /**
     * i-node index number
     */
    private long number;

    /**
     * create time
     */
    private long createTime;

    /**
     * last access time
     */
    private long lastAccessTime;

    /**
     * last modified time
     */
    private long lastModifiedTime;

    /**
     * first block number
     */
    private long firstBlockNumber;

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
}
