package com.github.futurefs.store.rpc;

/**
 * rpc枚举
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/01 17:52
 */
public enum RpcEnum {
    NETTY("netty"),
    GRPC("grpc");

    private String code;

    RpcEnum(String code) {
        this.code = code;
    }
}
