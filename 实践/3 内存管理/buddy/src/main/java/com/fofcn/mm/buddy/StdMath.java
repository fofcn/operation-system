package com.fofcn.mm.buddy;

/**
 * 数学函数
 *
 * @author jiquanxi
 * @date 2021/12/20
 */
public class StdMath {

    /**
     * 查找某个数字的下一个2的幂数值
     * @param a 数字
     * @return 下一个幂数对应的数字
     */
    public static final int nextPowerOf2(final int a) {
        int b = 1;
        while (b < a) {
            b = b << 1;
        }

        return b;
    }

    /**
     * 查找小于某个数的最大2的幂数
     * @param a 数字
     * @return 小于该数字的2的最大幂数对应的数字
     */
    public static final int prevPowerOf2(final int a) {
        int b = Integer.MAX_VALUE;
        while (b > a) {
            b = b >> 1;
        }

        return b;
    }
}
