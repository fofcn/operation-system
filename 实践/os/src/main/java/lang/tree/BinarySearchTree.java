package lang.tree;

import util.StdOut;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 二分搜索树
 *
 * @author jiquanxi
 * @date 2022/01/13
 */
public class BinarySearchTree<Key extends Comparable<Key>, Value> implements Tree<Key, Value> {

    private final AtomicInteger size = new AtomicInteger(0);

    private volatile Node root;

    public BinarySearchTree() {
        this.root = null;
    }

    /**
     * 获取树节点大小
     * @return 节点大小
     */
    @Override
    public int size() {
        return size.get();
    }

    /**
     * 树是否为空
     * @return true 为空， false不为空
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        return get(key) != null;
    }

    @Override
    public Value get(Key key) {
        // 参数检查
        if (key == null) {
            throw new IllegalArgumentException();
        }

        Node node = getInternal(key);

        return node == null ? null : node.value;
    }

    @Override
    public void put(Key key, Value value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        if (value == null) {
            delete(key);
            return;
        }

        putInternal(key, value);
    }

    @Override
    public void delete(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        Node tmp = root;
        while (tmp != null) {
            int cmp = key.compareTo(tmp.key);
            if (cmp < 0) {
                tmp = tmp.left;
            } else if (cmp > 0) {
                tmp = tmp.right;
            } else {
                // 找到了待删除的节点
                // 情况1：如果该节点既没有左孩子也没有右孩子，那么就直接删除
                // 情况2：如果该节点只有一个孩子那么直接将该孩子提升到该孩子的位置上
                // 情况3：如果该节点既有左孩子又有右孩子,则查找待删除节点的右孩子中最小的孩子，并将最小的孩子提升到待删除节点的位置

                // 左孩子为空，那么用右孩子替换为tmp
                if (tmp.left == null) {
                    transplant(tmp, tmp.right);
                } else if (tmp.right == null) {
                    // 右孩子为空，那么用左孩子替换掉tmp
                    transplant(tmp, tmp.left);
                } else {
                    // 左右孩子都不为空
                    // 查找右孩子中最小的节点
                    Node min = min(tmp.right);
                    // 右孩子中最小节点的父级节点不是tmp表示最小节点不是tmp的直接孩子
                    // 那么这种情况下，需要将最小节点的右孩子替换最小节点的位置
                    // 将最小节点的右孩子设置为tmp右孩子
                    // 将最小节点的右孩子的父设置为最小节点
                    if (min.parent != tmp) {
                        transplant(min, min.right);
                        min.right = tmp.right;
                        min.right.parent = min;
                    }

                    // 用tmp右子树最小节点替换掉tmp
                    // 最小节点的左孩子更新为tmp的左孩子
                    // 最小节点的左孩子的父节点更新为最小节点
                    transplant(tmp, min);
                    min.left = tmp.left;
                    min.left.parent = min;
                }
                tmp = null;
                break;
            }
        }
    }

    private void transplant(Node del, Node child) {
        // 更新待删除节点的父节点
        if (del.parent == null) {
            root = child;
        } else if (del == del.parent.left) {
            del.parent.left = child;
        } else {
            del.parent.right = child;
        }

        // 更新孩子节点的父节点为待删除节点的父节点
        if (child != null) {
            child.parent = del.parent;
        }
    }

    private void putInternal(Key key, Value value) {
        // 初始化二叉搜索树
        if (root == null) {
            root = new Node(key, value);
            size.incrementAndGet();
        } else {
            // 从根节点开始查找插入位置
            Node tmp = root;
            while (tmp != null) {
                int cmp = key.compareTo(tmp.key);
                // 如果待插入节点比当前节点小，那么查看该节点的左孩子是否为空，如果为空，则将待插入节点插入到左孩子
                if (cmp < 0) {
                    if (tmp.left == null) {
                        tmp.left = new Node(key, value);
                        tmp.left.parent = tmp;
                        size.incrementAndGet();
                        break;
                    }
                    tmp = tmp.left;
                    // 如果待插入节点比当前节点小，那么查看该节点的右孩子是否为空，如果为空，则将待插入节点插入到右孩子
                } else if (cmp > 0) {
                    if (tmp.right == null) {
                        tmp.right = new Node(key, value);
                        tmp.right.parent = tmp;
                        size.incrementAndGet();
                        break;
                    }
                    tmp = tmp.right;
                } else {
                    tmp.value = value;
                    break;
                }
            }
        }
    }

    /**
     * 根据key查找Key对应的节点
     * @param key 关键字
     * @return key对应的关键字，null没有找到关键字对应的节点
     */
    private Node getInternal(Key key) {
        Node tmp = root;
        int cmp;
        // 遍历树查找节点
        while (tmp != null && (cmp = key.compareTo(tmp.key)) != 0) {
            if (cmp < 0 ) {
                tmp = tmp.left;
            } else {
                tmp = tmp.right;
            }
        }
        return tmp;
    }

    private Node min(Node node) {
        Node tmp = node;
        while (tmp.left != null) {
            tmp = tmp.left;
        }

        return tmp;
    }

    private class Node {
        private Key key;

        private Value value;

        private Node left;

        private Node right;

        private Node parent;

        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }
}
