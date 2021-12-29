package fs.trivial.root;

import helper.annotation.SerializerOrder;

import java.util.List;

/**
 * Root directory of file system.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class RootDirectory {

    @SerializerOrder
    private String name;

    private List<Long> iNodeNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
