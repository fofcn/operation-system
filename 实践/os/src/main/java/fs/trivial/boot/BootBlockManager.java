package fs.trivial.boot;

import fs.trivial.CaSystem;
import fs.trivial.Manager;
import lang.serializer.ByteArraySerializer;

/**
 * boot block manager
 *
 * @author jiquanxi
 * @date 2021/12/29
 */
public class BootBlockManager implements Manager {

    private static final int FS_IS_INITIALIZED = 1;

    private static final int FS_NOT_INITIALIZED = 0;

    private static final int BOOT_PAGE_NUMBER = 0;

    private BootBlock bootBlock;

    private final CaSystem caSystem;

    public BootBlockManager(final CaSystem caSystem) {
        this.caSystem = caSystem;
    }

    @Override
    public boolean initialize() {
        // load first block,check if the file system had initialize.
        byte[] bootBlockBytes = caSystem.getDiskHelper().read(0, caSystem.getBlockSize());
        bootBlock = ByteArraySerializer.deserialize(BootBlock.class, bootBlockBytes);
        if (bootBlock == null ||  bootBlock.getIsInit() == FS_IS_INITIALIZED) {
            // if the file system is not initialize,initialize it.
            // Initialize the boot block of the file system
            bootBlock = new BootBlock();
            bootBlock.setIsInit(FS_NOT_INITIALIZED);

            // write boot block info to disk
            bootBlockBytes = ByteArraySerializer.serialize(bootBlock, BootBlock.class);
            caSystem.getDiskHelper().write(bootBlockBytes, 0);
        }

        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public boolean getIsInit() {
        return bootBlock.getIsInit() == FS_IS_INITIALIZED;
    }
}
