package com.github.futurefs.client.api;

import lombok.Data;

/**
 * 接口结果
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 12:08
 */
@Data
public class ApiResult<T> {
    private int code;

    private String msg;

    private T data;

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
