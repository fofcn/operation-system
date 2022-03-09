package lang.tree;

/**
 * 树操作接口定义
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/13
 */
public interface Tree<Key extends Comparable<Key>, Value> {

    void delete(Key key);

    void put(Key key, Value value);

    Value get(Key key);

    int size();

    boolean isEmpty();

    boolean contains(Key key);
}
