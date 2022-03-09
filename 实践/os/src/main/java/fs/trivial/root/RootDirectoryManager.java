package fs.trivial.root;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import lang.serializer.ByteArraySerializer;

/**
 * root directory manager
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/29
 */
public class RootDirectoryManager implements Manager {
    public static final String ROOT_DIR = "/";

    private final CaSystem caSystem;

    private RootDirectory rootDirectory;

    public RootDirectoryManager(final CaSystem caSystem) {
        this.caSystem = caSystem;
    }

    @Override
    public boolean initialize() {
        if (caSystem.getBootBlockManager().getIsInit()) {
            long rootDirStartPage = caSystem.getSuperBlockManager().getRootDirectoryStartPage();
            long rootDirPages = caSystem.getSuperBlockManager().getRootDirectoryPages();
            byte[] rootDirBytes = caSystem.getDiskHelper().read(rootDirStartPage * caSystem.getBlockSize(),
                    (int) (rootDirPages * caSystem.getBlockSize()));
            rootDirectory = ByteArraySerializer.deserialize(RootDirectory.class, rootDirBytes);
        } else {
            RootDirectory rootDirectory = new RootDirectory();
            rootDirectory.setName(ROOT_DIR);
            rootDirectory.setNameLength(ROOT_DIR.length());
        }

        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
