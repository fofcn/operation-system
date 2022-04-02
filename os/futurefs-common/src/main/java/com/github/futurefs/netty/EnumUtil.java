package com.github.futurefs.netty;

/**
 * 枚举工具类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 13:45
 */
public class EnumUtil {

    public static <T extends BaseEnum> T getByCode(T[] enums, int code) {
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode() == code) {
                return enums[i];
            }
        }

        return null;
    }
}
