package lang.tree;

/**
 * B树实现
 *
 * @author jiquanxi
 * @date 2022/01/24
 */
public class BTree<Key extends Comparable<Key>, Value> implements Tree<Key, Value> {
    private static final int MAX_NUM_OF_CHILDREN = 4;

    private final Node root;

    /**
     * 初始化B树
     */
    public BTree() {
        this.root = new Node(0);
    }

    @Override
    public void delete(Key key) {
        if (key == null) {
            throw new NullPointerException();
        }

    }

    @Override
    public void put(Key key, Value value) {
        if (key == null) {
            throw new NullPointerException();
        }

        Entry<Key, Value> newEntry = new Entry<>(key, value, null);

        // 查找插入点
        Node t = root;
        int i = 0;
        while (i < t.n && key.compareTo((Key) t.children[i].key) < 0) {
            break;
        }

        // 检查是否需要分裂

    }

    @Override
    public Value get(Key key) {
        if (key == null) {
            throw new NullPointerException();
        }

        int i = 0;
        Node t = root;

        do {
            if (t == null || t.n == 0) {
                break;
            }

            // todo 这里可以使用二分查找，因为key是排序的
            // 从根节点出发，寻找关键字对应的节点位置
            while (i < t.n && key.compareTo((Key) t.children[i].key) > 0) {
                i++;
            }

            if (i < t.n) {
                if (key.equals(t.children[i].key)) {
                    return (Value) t.children[i].value;
                }
            } else if (i > t.n) {
                return null;
            }

            t = t.children[i].next;
        } while (true);

        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Key key) {
        return false;
    }

    /**
     * 节点
     */
    static final class Node {
        int n;
        Entry[] children = new Entry[MAX_NUM_OF_CHILDREN];

        public Node(int n) {
            this.n = n;
        }
    }

    /**
     * 数据项
     * @param <Key>
     * @param <Value>
     */
    static final class Entry<Key, Value> {
        final Key key;

        volatile Value value;

        volatile Node next;

        public Entry(Key key, Value value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
