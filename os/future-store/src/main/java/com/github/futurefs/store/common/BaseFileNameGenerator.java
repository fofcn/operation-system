package com.github.futurefs.store.common;

/**
 * 基础存储文件名称生成
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/24 15:22
 */
public interface BaseFileNameGenerator {

    /**
     * 生成文件
     * @return 文件名称
     */
    String generator();
}
