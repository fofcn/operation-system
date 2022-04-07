package com.github.futurefs.store.guid;

/**
 * uid生成器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/07 14:16
 */
public interface UidGenerator<Type> {

    /**
     * 生成guid
     * @return guid
     */
    Type generate();
}
