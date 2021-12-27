package lang;

/**
 * BitMap implementation 位图实现
 *
 * @author jiquanxi
 * @date 2021/12/27
 */
public class BitMap {

    private final int size;

    private final int[] bitsMap;

    private static final int[] BIT_VALUE = {0x00000001, 0x00000002, 0x00000004, 0x00000008, 0x00000010, 0x00000020,
            0x00000040, 0x00000080, 0x00000100, 0x00000200, 0x00000400, 0x00000800, 0x00001000, 0x00002000, 0x00004000,
            0x00008000, 0x00010000, 0x00020000, 0x00040000, 0x00080000, 0x00100000, 0x00200000, 0x00400000, 0x00800000,
            0x01000000, 0x02000000, 0x04000000, 0x08000000, 0x10000000, 0x20000000, 0x40000000, 0x80000000};

    public BitMap(int size) {
        this.size = size;
        long buckets = size / 32 + size % 32 == 0 ? 0 : 1;
        bitsMap = new int[(int) buckets];
    }

    /**
     * 设置指定位的bit为1
     * @param n 设置位的索引
     */
    public void setBit(int n) {
        if (n < 0 || n > size) {
            throw new IllegalArgumentException("The length value " + n + "is illegal.");
        }

        // 查询数组的元素
        int index = n / 32;

        // 查询元素位的偏移
        int offset = n % 32 - 1;

        bitsMap[index] |= BIT_VALUE[offset];
    }

    public void clearBit(int n) {
        if (n < 0 || n > size) {
            throw new IllegalArgumentException("The length value " + n + "is illegal.");
        }

        // 查询数组的元素
        int index = n / 32;

        // 查询元素位的偏移
        int offset = n % 32 - 1;

        bitsMap[index] &= (~BIT_VALUE[offset]);
    }

    public boolean checkBit(int n) {
        // 查询数组的元素
        int index = n / 32;

        // 查询元素位的偏移
        int offset = n % 32 - 1;

        int idxVal = bitsMap[index];
        return (idxVal & BIT_VALUE[offset]) != 0;
    }

    public void clearAll() {
        for (int i = 0; i < bitsMap.length; i++) {
            bitsMap[i] &= 0;
        }
    }

    public void setAll() {
        for (int i = 0; i < bitsMap.length; i++) {
            bitsMap[i] |= 0xffffffff;
        }
    }
}
