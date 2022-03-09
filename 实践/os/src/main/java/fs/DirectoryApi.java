package fs;

/**
 * Directory operation API
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/27
 */
public interface DirectoryApi {
    int create(String name);

    int delete(String name);

    FileDescriptor openDir(String name);

    int closeDir(FileDescriptor fd);

    int readDir(FileDescriptor fd);

    int rename(String oldName, String newName);

    int link(String src, String dest);

    int unlink(String name);
}
