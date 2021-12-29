package fs.trivial.freespace;

import fs.trivial.Manager;
import lang.BitMap;

/**
 * a bit map structure for free space management.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class BitMapFreeSpaceManager implements FreeSpaceManager, Manager {
    private final BitMap freeSpaceBitMap;

    public BitMapFreeSpaceManager(final long blockAmount) {
        this.freeSpaceBitMap = new BitMap((int) blockAmount);
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
}
