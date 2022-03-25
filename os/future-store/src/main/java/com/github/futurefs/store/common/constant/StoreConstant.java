package com.github.futurefs.store.common.constant;

/**
 * 存储信息常量定义
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 11:22
 */
public interface StoreConstant {

    long INDEX_SUPER_MAGIC_NUMBER = Long.MAX_VALUE;

    long STORE_SUPER_MAGIC_NUMBER = Long.MAX_VALUE - 1;

    long BLOCK_HEADER_MAGIC_NUMBER = Long.MAX_VALUE - 2;

    long BLOCK_TAILOR_MAGIC_NUMBER = Long.MAX_VALUE - 3;

    long STORE_VERSION = 100L;

    String BLOCK_TOPIC_NAME = "fileBlock";
}
