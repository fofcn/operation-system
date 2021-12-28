package fs.trivial;

import lang.BitMap;

/**
 * a bit map structure for free space management.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class BitMapFreeSpaceManager implements FreeSpaceManager {
    private final BitMap freeSpaceBitMap;

    public BitMapFreeSpaceManager(final long blockAmount) {
        this.freeSpaceBitMap = new BitMap((int) blockAmount);
    }
}
