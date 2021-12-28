package fs;

import java.nio.ByteBuffer;

/**
 * File operation application interface.
 *
 * @author jiquanxi
 * @date 2021/12/27
 */
public interface FileApi {

    /**
     * Create a file and specified its name.
     * @param name file name
     * @return true means that file created success and false means failed.
     */
    boolean create(String name);

    /**
     * Delete the file which name is the name.
     * @param name the file name to delete.
     * @return true implies success and false is failed.
     */
    boolean delete(String name);

    /**
     * Open the file which name is the name.
     * @param name the file name is to create
     * @param mode open mod. RD_ONLY,WRONLY, APPEND
     * @return a file descriptor if the value greater than 0
     * but return 0 indicate failure.
     */
    FileDescriptor open(String name, int mode);

    /**
     * Close the opened file.
     * @param fd file descriptor.
     * @return 0 means success, others means failure.
     */
    int close(FileDescriptor fd);

    /**
     * Sets the file position indicator for the file stream to the value pointed by offset.
     * @param fd file descriptor to modify
     * @param offset number characters th shift the position relative to origin
     * @param origin position to which offset is added.It can have one of the following values: SEEK_SET,SEEK_CUR,SEEK_END
     * @return 0 upon success, nonzero value otherwise.
     */
    int seek(FileDescriptor fd, int offset, int origin);

    int read(FileDescriptor fd, ByteBuffer buffer);

    int write(FileDescriptor fd, ByteBuffer buffer);

    int setAttributes(int fd, FileAttribute fileAttribute);

    FileAttribute getAttributes(FileDescriptor fd);

    int rename(FileDescriptor fd, String newName);
}
