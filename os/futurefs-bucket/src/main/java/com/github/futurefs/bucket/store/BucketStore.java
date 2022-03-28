package com.github.futurefs.bucket.store;

/**
 * bucket存储
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 13:46
 */
public interface BucketStore {
    /**
     * 创建bucket
     * @param bucket
     */
    void create(Bucket bucket);

    /**
     * 获取bucket
     * @param name
     * @return
     */
    Bucket get(String name);

    /**
     * 删除bucket
     * @param bucket
     */
    void delete(Bucket bucket);
}
