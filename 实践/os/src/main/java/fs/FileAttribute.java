package fs;

/**
 * 文件属性定义 Definition of file attributes.
 *
 * @author jiquanxi
 * @date 2021/12/27
 */
public interface FileAttribute {

    /**
     * 设置最后访问时间
     */
    void setLastAccessTime();

    /**
     * 获取最后访问时间
     * @return 最后访问时间
     */
    long getLastAccessTime();

    /**
     * 设置最后修改时间
     */
    void setLastModifiedTime();

    /**
     * 获取最后修改时间
     * @return 最后修改时间
     */
    long getLastModifiedTime();

    /**
     * 设置创建时间
     */
    void setCreateTime();

    /**
     * 获取创建时间
     * @return 创建时间
     */
    long getCreateTime();

    /**
     * 获取文件大小
     * @return 文件大小
     */
    long getSize();

    /**
     * 设置文件大小
     */
    void setSize();
}
