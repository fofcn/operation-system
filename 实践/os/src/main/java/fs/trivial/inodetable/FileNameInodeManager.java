package fs.trivial.inodetable;

import fs.trivial.CaSystem;
import fs.trivial.Manager;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * file name and inode map manager
 *
 * @author jiquanxi
 * @date 2021/12/29
 */
public class FileNameInodeManager implements Manager {

    private final CaSystem caSystem;

    private final long dataStartPage;

    private final long dataPages;

    public FileNameInodeManager(final CaSystem caSystem) {
        this.caSystem = caSystem;
        this.dataStartPage = caSystem.getSuperBlockManager().getFileInodeStartPage();
        this.dataPages = caSystem.getSuperBlockManager().getFileInodePages();
    }

    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public long getInodeNumber(String name) {
        if (caSystem.getSuperBlockManager().getInodeAmount() == 0L) {
            return -1L;
        }

        long offset = dataStartPage * caSystem.getBlockSize();
        long end = offset + dataPages * caSystem.getBlockSize();

        for (long i = offset; i <= end;) {
            byte[] bytes = caSystem.getDiskHelper().read(i, 12);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            long inodeNumber = buffer.getLong();
            int nameLength = buffer.getInt();
            bytes = caSystem.getDiskHelper().read(i + 12, nameLength);
            String fileName = new String(bytes, StandardCharsets.UTF_8);
            if (name.equals(fileName)) {
                return inodeNumber;
            }
            i = i + 12 + nameLength;
        }

        return -1L;
    }

    public void createFileInode(int index, long inodeNumber, String name) {
    }
}
