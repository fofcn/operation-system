package com.github.futurefs.netty;

/**
 * BaseResult包装类
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 12:16
 */
public class RWrapper {

    public static boolean isSuccess(R result) {
        return result != null && result.getCode() == ResultCode.SUCCESS;
    }

    public static boolean isFailed(R result) {
        return result == null && result.getCode() != ResultCode.SUCCESS;
    }

    public static <T> R<T> success(T data) {
        return new R<>(ResultCode.SUCCESS, 0, "ok", data);
    }

    public static <T> R<T> fail() {
        return new R<>(ResultCode.FAIL, 0, "fail", null);
    }
}
