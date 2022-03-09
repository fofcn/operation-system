package fs.trivial.inodetable;

import helper.annotation.FixedByteSerializer;
import helper.annotation.SerializerOrder;

/**
 * file name and inode table.
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
public class FileNameInode implements FixedByteSerializer {
    @SerializerOrder(1)
    private long inodeNumber;

    @SerializerOrder(2)
    private int nameLength;

    @SerializerOrder(3)
    private String name;

    public long getInodeNumber() {
        return inodeNumber;
    }

    public void setInodeNumber(long inodeNumber) {
        this.inodeNumber = inodeNumber;
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
