package com.github.futurefs.bucket.store.fs;

import lombok.Data;

import java.util.Set;

/**
 * 数据节点
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 14:14
 */
@Data
public class DataNode {

    /**
     * 子节点
     */
    private Set<String> children;
}
