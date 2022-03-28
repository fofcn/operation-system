package com.github.futurefs.netty.enums;

/**
 * 响应码定义
 *
 * @author errorfatal89@gmail.com
 */
public enum ResponseCode {
    SUCCESS(0, "成功"),
    SYSTEM_ERROR(1, "系统错误"),
    SYSTEM_BUSY(2, "系统繁忙"),
    REQUEST_CODE_NOT_SUPPORTED(3, "不支持的命令码"),
    TRANSACTION_FAILED(4, "业务失败"),
    INTERCEPTOR_FAILED(5, "过滤消息失败");

    private int code;

    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
