package lang.skiplists;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 跳跃表实现
 *
 * @author jiquanxi
 * @date 2022/01/14
 */
public class SkipLists<Key extends Comparable<Key>, Value> {

    public static final int MAX_LEVEL = 32;

    private static final Object BASE_OBJECT = new Object();

    private static final int PROBABILITY = 50;

    private volatile HeaderIndex<Key, Value> header;


    public SkipLists() {
        this.header = new HeaderIndex(new Node(null, BASE_OBJECT, null), null, null, 1);
    }

    /**
     * 根据key从跳跃表中获取一个值
     * @param key key
     * @return 存在则返回对应的value，不存在则返回null
     */
    public Value get(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        Node<Key, Value> predecessor = findPredecessor(key);
        for (Node<Key, Value> next = predecessor.next;;) {
            if (next == null) {
                break;
            }

            int cmp = key.compareTo(next.key);
            if (cmp == 0) {
                return next.value;
            }

            next = next.next;
        }

        return null;
    }

    /**
     * 向跳跃表添加一个key-value对
     * @param key key
     * @param value value
     */
    public void put(Key key, Value value) {
        if (key == null) {
            throw new NullPointerException();
        }

        Node<Key, Value> newNode = null;
        // 找到最底层插入节点的前驱节点
        Node<Key, Value> predecessor = findPredecessor(key);

        // 找到索引节点对应的数据节点以后，开始查找插入数据前驱节点
        for (Node<Key, Value> b = predecessor, next = b.next;;) {
            if (next != null) {
                // 如果插入的key大于当前数据节点，那么继续查找下一个
                int cmp = key.compareTo(next.key);
                if (cmp > 0) {
                    b = next;
                    next = next.next;
                    continue;
                } else if (cmp == 0) {
                    next.value = value;
                    break;
                }
            }

            newNode = new Node<>(key, value, next);
            b.next = newNode;
            break;
        }

        // 是否要为数据节点添加索引层
        int level = randomLevel();

        // 需要加层
        if (level > header.level) {
            int oldLevel = header.level;
            int newLevel = level;

            // 为新节点创建索引节点
            Index<Key, Value>[] newNodeIndexes = createNewNodeIndex(newNode, newLevel);
            // 为头结点增量补充索引节点,并将头结点的索引节点指向新节点的索引节点
            header = incrHeaderIdxes(newLevel, newNodeIndexes);

            // 根据老层更新新节点的数据
            updateIndex(key, newNodeIndexes[oldLevel], oldLevel, newLevel);
        } else {
            // 如果节点索引层大于1就需要为节点新建索引层
            if (level > 1) {
                // 根据新节点索引层新建节点索引
                Index<Key, Value>[] newNodeIndexes = createNewNodeIndex(newNode, level);
                // 更新索引
                // 在没有新建层时，为新节点新建层传入的新层参数是头索引的层数，因为每次都从头索引开始查找，
                // 需要将头索引直接下降到对应的层后开始修改关系
                updateIndex(key, newNodeIndexes[level], level, header.level);
            }
        }
    }

    /**
     * 根据key删除一个节点
     * 注意删除节点可能需要减层
     * @param key 要删除的关键字
     * @return key对应的value值，如果没有找到value就返回null
     */
    public Value delete(Key key) {
        if (key == null) {
            throw new NullPointerException();
        }

        Value val = null;

        // 找到最底层插入节点的前驱节点
        Node<Key, Value> predecessor = findPredecessor(key);
        for (Node<Key, Value> b = predecessor, next = b.next;;) {
            if (next != null) {
                // 如果插入的key大于当前数据节点，那么继续查找下一个
                int cmp = key.compareTo(next.key);
                if (cmp > 0) {
                    b = next;
                    next = next.next;
                    continue;
                } else if (cmp < 0) {
                    break;
                } else {
                    // 相等就将节点元素设置为空
                    val = next.value;
                    next.value = null;
                    break;
                }
            }

            break;
        }

        findPredecessor(key);
        // 删除层
        while (header.right == null && header.level > 1) {
            header = (HeaderIndex<Key, Value>) header.down;
        }

        return val;
    }

    /**
     * 查找key对应的前驱索引
     * @param key key
     * @return 前驱索引
     */
    private Index<Key, Value> findIndex(Key key) {
        for (Index<Key, Value> cur = header, right = cur.right, down;;) {
            // 如果索引的右侧不为空
            // 用搜索的key对数据节点的key进行比较
            // 如果搜索的key大于索引节点的key，那么继续向右进行搜索
            if (right != null) {
                Node<Key, Value> n = right.node;
                Key k = n.key;
                // value为空代表节点的值已经被删除
                // 删除节点对应的索引
                if (n.value == null) {
                    // 将当前所有的右侧索引更新为右侧的右侧
                    cur.right = right.right;
                    // 更新right索引变量为当前索引的右侧
                    right = cur.right;
                    continue;
                }

                // 如果搜索的key大于右侧节点指向的key，那么继续向右查找
                if (key.compareTo(k) > 0) {
                    cur = right;
                    right = right.right;
                    continue;
                }
            }

            // 如果索引节点的右侧节点大于key，那么向下放索引查找
            if ((down = cur.down) != null) {
                // 将当前节点指向下方索引节点
                // 右侧节点指针指向下方索引的右侧
                cur = down;
                right = down.right;
            } else {
                return cur;
            }
        }
    }

    /**
     * 查找key对应的前驱节点
     * @param key key
     * @return 前驱节点
     */
    private Node<Key, Value> findPredecessor(Key key) {
        Index<Key, Value> index = findIndex(key);
        return index.node;
    }

    /**
     * 更新老层的索引
     * @param key 关键字
     * @param newNodeOldIdx 新节点索引
     * @param oldLevel 老层数
     * @param newLevel 新层数
     */
    private void updateIndex(Key key, Index<Key, Value> newNodeOldIdx, int oldLevel, int newLevel) {
        Index<Key, Value> newNodeIdx = newNodeOldIdx;
        Index<Key, Value> precursorIdx = header;

        // 跳过新索引层，因为已经做了关联
        for (int i = oldLevel + 1; i <= newLevel; i++) {
            precursorIdx = precursorIdx.down;
        }
        Index<Key, Value> right = precursorIdx.right;

        // 找到对应的层之后，我们开始向右继续查找前驱索引节点
        while (true) {
            if (right != null && right.node.value != null) {
                int cmp = key.compareTo((Key) right.node.key);
                if (cmp > 0) {
                    precursorIdx = right;
                    right = right.right;
                    continue;
                }
            }

            // 找到需要更新的索引之后，重建索引
            // 前驱索引节点的右侧设置新的索引
            precursorIdx.right = newNodeIdx;
            // 新索引有右侧设置为老索引的右侧节点
            newNodeIdx.right = right;
            // 新节点索引向下
            newNodeIdx = newNodeIdx.down;
            // 老索引向下
            precursorIdx = precursorIdx.down;
            if (precursorIdx == null) {
                break;
            }
        }
    }

    /**
     * 随机生成节点的层，但是不超过32
     * @return 新节点层数
     */
    private int randomLevel() {
        int level = 1;
        int rnd = ThreadLocalRandom.current().nextInt(1, 101);
        while (rnd > PROBABILITY && level <= MAX_LEVEL) {
            // 根据随机数来生成索引层数
            ++level;
            rnd = ThreadLocalRandom.current().nextInt(1, 101);
        }
        return level;
    }

    /**
     * 为头结点补充索引层级
     * @param newLevel 新层数
     * @param newNodeIdxes 新节点索引数组
     * @return 新的头结点
     */
    private HeaderIndex<Key, Value> incrHeaderIdxes(int newLevel, Index<Key, Value>[] newNodeIdxes) {
        HeaderIndex<Key, Value> newHeader = header;
        for (int i = header.level + 1; i <= newLevel; i++) {
            newHeader = new HeaderIndex<>(header.node, newHeader, newNodeIdxes[i], i);
        }

        return newHeader;
    }

    /**
     * 为新节点创建索引节点，并建立向下的索引关系
     * @param newNode 新节点
     * @param newLevel 层数
     * @return 新节点的索引节点
     */
    private Index<Key,Value>[] createNewNodeIndex(Node<Key,Value> newNode, int newLevel) {
        Index<Key,Value>[] newNodeIdxes = new Index[newLevel + 1];
        Index<Key,Value> newIndex = null;
        for (int i = 1; i <= newLevel; i++) {
            newIndex = new Index<>(newNode, newIndex, null);
            newNodeIdxes[i] = newIndex;
        }

        return newNodeIdxes;
    }

    /**
     * 数据节点
     */
    static class Node<Key, Value> {

        /**
         * 键
         */
        final Key key;

        /**
         * 值
         */
        volatile Value value;

        /**
         * 链表指针
         */
        volatile Node<Key, Value> next;

        public Node(Key key, Value value, Node<Key, Value> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 索引
     */
    static class Index<K,V> {
        final Node<K,V> node;
        volatile Index<K,V> right;
        volatile Index<K,V> down;

        public Index(Node<K,V> node, Index<K,V> down, Index<K,V> right) {
            this.node = node;
            this.right = right;
            this.down = down;
        }
    }

    /**
     * 头
     */
    static class HeaderIndex<K,V> extends Index<K,V> {
        private final int level;

        public HeaderIndex(Node<K,V> node, Index<K,V> down, Index<K,V> right, int level) {
            super(node, down, right);
            this.level = level;
        }
    }
}
