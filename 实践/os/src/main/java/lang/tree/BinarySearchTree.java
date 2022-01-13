package lang.tree;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 二分搜索树
 *
 * @author jiquanxi
 * @date 2022/01/13
 */
public class BinarySearchTree<Key extends Comparable<Key>, Value> {

    private final AtomicInteger size = new AtomicInteger(0);

    private volatile Node root;

    public BinarySearchTree() {
        this.root = null;
    }

    /**
     * 获取树节点大小
     * @return 节点大小
     */
    public int size() {
        return size.get();
    }

    /**
     * 树是否为空
     * @return true 为空， false不为空
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        return get(key) != null;
    }

    public Value get(Key key) {
        // 参数检查
        if (key == null) {
            throw new IllegalArgumentException();
        }

        Node node = getInternal(key);

        return node == null ? null : node.value;
    }

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
                // 情况3：如果该节点既有左孩子又有右孩子
                if (tmp.left == null) {
                    transplant(tmp, tmp.right);
                } else if (tmp.right == null) {
                    transplant(tmp, tmp.left);
                } else {
                    Node min = min(tmp.right);
                    if (min.parent != tmp) {
                        transplant(min, min.right);
                        min.right = tmp.right;
                        min.right.parent = min;
                    }

                    transplant(tmp, min);
                    min.left = tmp.left;
                    min.right.parent = min;
                }
                tmp = null;
                break;
            }
        }
    }

    private void transplant(Node del, Node child) {
        if (del.parent == null) {
            root = child;
        } else if (del == del.parent.left) {
            del.parent.left = child;
        } else {
            del.parent.right = child;
        }

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
            Node tmp = root;
            while (tmp != null) {
                int cmp = key.compareTo(tmp.key);
                if (cmp < 0) {
                    if (tmp.left == null) {
                        tmp.left = new Node(key, value);
                        tmp.left.parent = tmp;
                        size.incrementAndGet();
                        break;
                    }
                    tmp = tmp.left;
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

    private Node getInternal(Key key) {
        Node ret = null;
        Node tmp = root;
        // 遍历树查找节点
        while (tmp != null) {
            int cmp = key.compareTo(tmp.key);
            if (cmp < 0 ) {
                tmp = tmp.left;
            } else if (cmp > 0) {
                tmp = tmp.right;
            } else {
                ret = tmp;
                break;
            }
        }
        return ret;
    }

    private Node min(Node node) {
        Node tmp = node;
        while (tmp.left != null) {
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
