package com.github.futurefs.common;

import lombok.Data;

/**
 * 结果处理
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 12:14
 */
@Data
public class R<T> {

    /**
     * 结果码
     */
    private int code;

    /**
     * 业务编码
     */
    private int bizCode;

    /**
     * 描述
     */
    private String msg;

    /**
     * 数据，如果有
     */
    private T data;

    public R(int code, int bizCode, String msg, T data) {
        this.code = code;
        this.bizCode = bizCode;
        this.msg = msg;
        this.data = data;
    }
}
