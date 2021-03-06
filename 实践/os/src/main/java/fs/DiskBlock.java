package fs;

/**
 * Disk block interface.
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/27
 */
public interface DiskBlock {

    /**
     * set block size
     * @param size block size
     */
    void setBlockSize(int size);

    /**
     * get block size
     * @return block size
     */
    int getBlockSize();
}
