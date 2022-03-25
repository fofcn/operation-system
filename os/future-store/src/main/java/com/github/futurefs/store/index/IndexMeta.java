package com.github.futurefs.store.index;

import lombok.Data;

/**
 * 索引元数据
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:31:00
 */
@Data
public class IndexMeta {

    private long key;

    private long offset;

    private long size;

    private long deleteStatus;
}
