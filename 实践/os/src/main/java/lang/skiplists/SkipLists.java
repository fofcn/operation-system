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

    private volatile Header<Key, Value> header;


    public SkipLists() {
        this.header = new Header(new Node(null, null, null), null, null, 1);
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
        int rnd = ThreadLocalRandom.current().nextInt();
        // test highest and lowest bits
        if (rnd > probability) {

            // 根据随机数来生成索引层数
            int level = 1, max;
            while (((rnd >>>= 1) & 1) != 0) {
                ++level;
            }

            Index<Key,Value> idx = null;
            Header<Key,Value> h = header;
            // 如果生成的索引层数小于等于当前的最高的索引层数，那么就为新建节点新建所有层的索引
            if (level <= (max = h.level)) {
                for (int i = 1; i <= level; ++i) {
                    idx = new Index<>(newNode, idx, null);
                }
            } else {
                // 添加新的索引层，最新的索引层索引需要重建
                level = max + 1;

                // 为新添加的新节点新建索引节点
                // 新节点索引节点有多少层就需要新建多少个索引节点
                Index<Key, Value>[] idxs = new Index[level + 1];
                for (int i = 1; i <= level; i++) {
                    idxs[i] = idx = new Index<>(newNode, idx, null);
                }

                // 为头索引增量添加层
                Header<Key, Value> newHeader = header;
                int oldLevel = header.level;
                for (int j = oldLevel + 1; j <= level; j++) {
                    newHeader = new Header<>(header.node, newHeader, idxs[j], j);
                }

                // 设置新的头
                header = newHeader;
                // 索引
                idx = idxs[level = oldLevel];
            }
        }

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
    static class Header<K,V> extends Index<K,V> {
        private final int level;

        public Header(Node node, Index down, Index right, int level) {
            super(node, right, down);
            this.level = level;
        }
    }
}
