package fs.trivial.root;

import helper.annotation.SerializerOrder;

/**
 * Root directory of file system.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class RootDirectory {

    @SerializerOrder(1)
    private int nameLength;

    @SerializerOrder(2)
    private String name;

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
