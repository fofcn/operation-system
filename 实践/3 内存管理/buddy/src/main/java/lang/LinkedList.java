package lang;

import java.util.Iterator;

/**
 * 双向链表
 *
 * @author jiquanxi
 * @date 2021/12/20
 */
public class LinkedList<E> implements Iterable<E> {
    private Node first;

    private Node last;

    /**
     * 添加节点到最后
     * @param e 数据
     */
    public Node<E> add(E e) {
        Node<E> node = new Node<E>(null, e, null);
        if (first == null) {
            first = node;
            last = node;
        } else {
            node.prev = last;
            last.next = node;
            last = node;
        }

        return node;
    }

    /**
     * 删除指定节点
     * @param old 老节点
     * @param replaces 新节点数组
     * @return 该节点的上一节点
     */
    public void replace(E old, E... replaces) {
        // 构建子序列
        for (Node<E> node = first; node != null; node = node.next) {
            if (node.item == old) {
                // 找到上一节点和下一节点
                Node<E> prev = node.prev;
                Node<E> next = node.next;

                Node<E> firstNode = null;
                Node<E> prevNode = node;
                for (int i = 0; i < replaces.length; i++) {
                    Node<E> newNode = new Node<E>(null, replaces[i], null);
                    prevNode.next = newNode;
                    newNode.prev = prevNode;
                    newNode.next = next;
                    prevNode = newNode;
                    if (i == 0) {
                        firstNode = newNode;
                    }
                }

                if (prev == null) {
                    first = firstNode;
                    first.prev = null;
                } else {
                    prev.next = firstNode;
                }

                if (next == null) {
                    last = prevNode;
                } else {
                    next.prev = prevNode;
                }

                break;
            }
        }
    }

    public Node<E> findNode(E e) {
        for (Node<E> node = first; node != null; node = node.next) {
            if (node.item == e) {
                return node;
            }
        }

        return null;
    }

    public void remove(Node<E> node) {
        Node<E> prev = node.prev;
        Node<E> next = node.next;

        if (prev != null) {
            prev.next = next;
        }

        if (next != null) {
            next.prev = prev;
        }
    }

    public Iterator<E> iterator() {
        return new LinkedListIterator(first);
    }

    class LinkedListIterator implements Iterator<E> {
        private Node<E> cur;

        public LinkedListIterator(Node<E> first) {
            cur = first;
        }

        public boolean hasNext() {
            return cur != null;
        }

        public E next() {
            E item = cur.item;
            cur = cur.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("This is not implemented.");
        }
    }

    public static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}
