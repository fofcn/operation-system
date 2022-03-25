package com.github.futurefs.store.common;

import lombok.Data;

/**
 * 读取文件
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/24 14:36
 */
@Data
public class ReadResult {
    /**
     * 文件内容
     */
    private byte[] content;
}
