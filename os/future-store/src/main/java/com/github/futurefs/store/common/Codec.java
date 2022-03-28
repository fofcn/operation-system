package com.github.futurefs.store.common;

import java.nio.ByteBuffer;

/**
 * 编解码
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 11:58
 */
public interface Codec<T> {
    /**
     * 编码
     * @return
     */
    ByteBuffer encode();

    /**
     * 解码
     * @param buffer
     * @return
     */
    T decode(ByteBuffer buffer);
}
