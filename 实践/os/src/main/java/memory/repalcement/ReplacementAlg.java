package memory.repalcement;

/**
 * Replacement algorithm 置换算法
 *
 * @author jiquanxi
 * @date 2021/12/23
 */
public interface ReplacementAlg<T> {

    /**
     * Cache a object.
     * @param obj
     */
    void cache(T obj);

    /**
     * Get a cached object.
     * @param key
     * @return
     */
    T get(T key);
}
