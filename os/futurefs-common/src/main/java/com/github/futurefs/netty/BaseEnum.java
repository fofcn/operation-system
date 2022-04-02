package com.github.futurefs.netty;

/**
 * 枚举接口
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 13:38
 */
public interface BaseEnum<T extends Enum<T>> {

    /**
     * 获取编码
     * @return
     */
    int getCode();

    /**
     * 获取描述
     * @return
     */
    String getDesc();
}
