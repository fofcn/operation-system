package lang.skiplists;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 跳跃表实现
 *
 * @author jiquanxi
 * @date 2022/01/14
 */
public class SkipLists<Key extends Comparable<Key>, Value> {
    private final AtomicInteger level = new AtomicInteger(1);

    private final int probability = 50;

    private volatile HeaderIndex<Key, Value> header;


    public SkipLists() {
        this.header = new HeaderIndex(new Node(null, null, null), null, null, 1);
    }

    /**
     * 根据key从跳跃表中获取一个值
     * @param key
     * @return
     */
    public Value get(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        Node<Key, Value> predecessor = findPredecessor(key);
        for (Node<Key, Value> node = predecessor, next = node.next;;) {
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
                }

                // key已经存在，替换value
                if (cmp == 0) {
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
            int newLevel = header.level + 1;

            // 为新节点创建索引节点
            Index<Key, Value>[] newNodeIndexes = createNewNodeIndex(newNode, newLevel);
            // 为头结点增量补充索引节点,并将头结点的索引节点指向新节点的索引节点
            header = incrHeaderIdxes(newLevel, newNodeIndexes);

            // 根据老层更新新节点的数据
            updateOldIdxes(oldLevel, newLevel);
        }
    }

    /**
     * 更新老层的索引
     * @param key 关键字
     * @param oldLevel 老层数
     * @param newLevel 新层数
     */
    private void updateOldIdxes(Key key, int oldLevel, int newLevel) {
        int nl = newLevel;
        Index<Key, Value> idx = header;
        Index<Key, Value> right = idx.right;
        do {
            // 从左向右查找更新点
            if (right != null) {
                int cmp = key.compareTo((Key) right.node.key);
                if (cmp > 0) {
                    idx = right;
                    right = right.right;
                    continue;
                }
            }

            // 找到更新点
            if (oldLevel == nl) {

            }

            nl--;
            if (nl >= oldLevel) {

            }

            idx = idx.down;
            right = idx.right;
        } while(true);
    }

    private int randomLevel() {
        int level = 1;
        int rnd = ThreadLocalRandom.current().nextInt(1, 101);
        while (rnd > probability && level <= 32) {
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
        for (int i = newHeader.level + 1; i <= newLevel; i++) {
            newHeader = new HeaderIndex<>(newHeader.node, newHeader, newNodeIdxes[i], i);
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
        Index<Key,Value>[] newNodeIdxes = new Index[newLevel];
        Index<Key,Value> newIndex = null;
        for (int i = 1; i <= newLevel; i++) {
            newIndex = new Index<>(newNode, newIndex, null);
            newNodeIdxes[i] = newIndex;
        }

        return newNodeIdxes;
    }

    public void delete(Key key) {

    }

    /**
     *
     * @param key
     * @return
     */
    private Node<Key, Value> findPredecessor(Key key) {
        //
        // 从头索引开始向右遍历头节点
        for (Index<Key, Value> q = header, right = q.right, down;;) {
            // 如果索引的右侧不为空
            // 用搜索的key对数据节点的key进行比较
            // 如果搜索的key大于索引节点的key，那么继续向右进行搜索
            if (right != null) {
                Node<Key, Value> n = right.node;
                Key k = n.key;
                if (n.value == null) {
                    right = q.right;
                    continue;
                }

                // 如果搜索的key大于右侧节点指向的key，那么继续向右查找
                if (key.compareTo(k) > 0) {
                    q = right;
                    right = right.right;
                    continue;
                }
            }

            // 如果索引节点的右侧节点大于key，那么向下放索引查找
            if ((down = q.down) != null) {
                // 将当前节点指向下方索引节点
                // 右侧节点指针指向下方索引的右侧
                q = down;
                right = down.right;
            } else {
                return q.node;
            }
        }
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
        volatile Node next;

        public Node(Key key, Value value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 索引
     */
    static class Index<K,V> {
        final Node node;
        volatile Index<K,V> right;
        volatile Index<K,V> down;

        public Index(Node node, Index down, Index right) {
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

        public HeaderIndex(Node node, Index down, Index right, int level) {
            super(node, right, down);
            this.level = level;
        }
    }
}
