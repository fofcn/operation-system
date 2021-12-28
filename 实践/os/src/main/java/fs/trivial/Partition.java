package fs.trivial;

import helper.annotation.SerializerOrder;

/**
 * Partition
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class Partition {
    /**
     * partition index
     */
    @SerializerOrder(1)
    private int index;

    /**
     * partition start offset
     */
    @SerializerOrder(2)
    private long start;

    /**
     * partition end offset
     */
    @SerializerOrder(2)
    private long end;

    /**
     * name length of partition.
     * We can read name of partition by using name length.
     */
    @SerializerOrder(4)
    private int nameLength;

    /**
     * partition name
     */
    @SerializerOrder(5)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getNameLength() {
        return nameLength;
    }

    public void setNameLength(int nameLength) {
        this.nameLength = nameLength;
    }
}
