package fs.trivial;

import helper.annotation.SerializerOrder;

/**
 * super block 超级块
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class SuperBlock {

    @SerializerOrder(1)
    private int magic;

    /**
     * the amount of blocks.
     */
    @SerializerOrder(2)
    private long blockAmount;

    /**
     * the start block number for data storing.
     */
    @SerializerOrder(3)
    private long dataStartBlockNumber;

    /**
     * i-node amount
     */
    @SerializerOrder(4)
    private long iNodeAmount;

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public long getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(long blockAmount) {
        this.blockAmount = blockAmount;
    }

    public long getDataStartBlockNumber() {
        return dataStartBlockNumber;
    }

    public void setDataStartBlockNumber(long dataStartBlockNumber) {
        this.dataStartBlockNumber = dataStartBlockNumber;
    }

    public long getiNodeAmount() {
        return iNodeAmount;
    }

    public void setiNodeAmount(long iNodeAmount) {
        this.iNodeAmount = iNodeAmount;
    }
}
