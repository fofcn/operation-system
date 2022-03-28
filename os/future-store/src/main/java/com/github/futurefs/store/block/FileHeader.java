package com.github.futurefs.store.block;

import com.github.futurefs.store.common.constant.StoreConstant;
import lombok.Data;

/**
 * 文件头
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/23
 */
@Data
public class FileHeader {

    /**
     * 文件起始魔数，用于恢复索引数据
     */
    private long headerMagic = StoreConstant.BLOCK_HEADER_MAGIC_NUMBER;

    /**
     * 删除状态
     */
    private long deleteStatus;

    /**
     * 文件校验和
     */
    private long crc64Number;

    /**
     * 文件名
     */
    private long key;

    /**
     * 文件长度
     */
    private long length;

}
