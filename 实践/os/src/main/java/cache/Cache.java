package cache;

/**
 * 缓存接口定义
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public interface Cache<K, V> {

    /**
     * 增加缓存
     * @param k 缓存key
     * @param v 缓存value
     */
    void set(K k, V v);

    /**
     * 从缓存中根据key获取一个值
     * @param k 缓存key值
     * @return
     */
    V get(K k);

    /**
     * 缓存大小
     * @return
     */
    int size();

    /**
     * 缓存全部清空
     */
    void clear();
}
