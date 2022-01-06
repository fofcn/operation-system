package memory.repalcement;

/**
 * Replacement algorithm 置换算法
 *
 * @author jiquanxi
 * @date 2021/12/23
 */
public interface ReplacementAlg<T> {

    /**
     * 缓存一个对象
     * Cache a object.
     * @param obj
     */
    void set(T obj);

    /**
     * 从缓存中获取一个对象
     * Get a cached object.
     * @param key key关键字
     * @return
     */
    T get(T key);
}
