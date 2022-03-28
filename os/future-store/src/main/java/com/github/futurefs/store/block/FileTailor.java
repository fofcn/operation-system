package com.github.futurefs.store.block;

import com.github.futurefs.store.common.constant.StoreConstant;
import lombok.Data;

/**
 * 文件尾部
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/23
 */
@Data
public class FileTailor {
    /**
     * 文件块尾部魔数
     */
    private long tailorMagic = StoreConstant.BLOCK_TAILOR_MAGIC_NUMBER;
}
