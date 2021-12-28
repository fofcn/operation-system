package fs.trivial;

/**
 * file name and inode table.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class FileNameInode {
    private long iNodeNumber;

    private int nameLength;

    private String name;

    public long getiNodeNumber() {
        return iNodeNumber;
    }

    public void setiNodeNumber(long iNodeNumber) {
        this.iNodeNumber = iNodeNumber;
    }

    public int getNameLength() {
        return nameLength;
    }

    public void setNameLength(int nameLength) {
        this.nameLength = nameLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
