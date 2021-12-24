package lang.map;


/**
 * Hash tables 接口
 *
 * @author jiquanxi
 * @date 2021/12/24
 */
public interface Map<Key, Value> {
    /**
     * 存
     * @param key
     * @param value
     */
    void put(Key key, Value value);

    /**
     * 取
     * @param key
     * @return
     */
    Value get(Key key);

    /**
     * 获取数量
     * @return
     */
    int size();

    /**
     * 删除指定key
     * @param key
     * @return
     */
    Value remove(Key key);

}
