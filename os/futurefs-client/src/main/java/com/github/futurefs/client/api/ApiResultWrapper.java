package com.github.futurefs.client.api;

/**
 * Api结果
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 17:12
 */
public class ApiResultWrapper {

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(0, "ok", data);
    }

    public static <T> ApiResult<T> fail(String msg) {
        return new ApiResult<>(1, msg, null);
    }

    public static boolean isSuccess(ApiResult<Long> fileKey) {
        return fileKey != null && fileKey.getCode() == 0;
    }
}
