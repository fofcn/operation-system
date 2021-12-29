package fs.trivial.api;

import fs.FileApi;
import fs.FileAttribute;
import fs.FileDescriptor;

import java.nio.ByteBuffer;

/**
 * implementation of file api
 *
 * @author jiquanxi
 * @date 2021/12/29
 */
public class CaFileApiImpl implements FileApi {

    @Override
    public boolean create(String name) {
        return false;
    }

    @Override
    public boolean delete(String name) {
        return false;
    }

    @Override
    public FileDescriptor open(String name, int mode) {
        return null;
    }

    @Override
    public int close(FileDescriptor fd) {
        return 0;
    }

    @Override
    public int seek(FileDescriptor fd, int offset, int origin) {
        return 0;
    }

    @Override
    public int read(FileDescriptor fd, ByteBuffer buffer) {
        return 0;
    }

    @Override
    public int write(FileDescriptor fd, ByteBuffer buffer) {
        return 0;
    }

    @Override
    public int setAttributes(int fd, FileAttribute fileAttribute) {
        return 0;
    }

    @Override
    public FileAttribute getAttributes(FileDescriptor fd) {
        return null;
    }

    @Override
    public int rename(FileDescriptor fd, String newName) {
        return 0;
    }
}
