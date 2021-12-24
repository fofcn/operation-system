package lang;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 双端队列实现
 * @author jiquanxi
 * @param <Item>
 */
public class Deque<Item> implements Iterable<Item> {

    private int size = 0;
    private Node<Item> first;
    private Node<Item> last;

    /**
     * 构造一个双端队列
     */
    public Deque() {

    }

    /**
     * 检查队列是否为空
     * @return true:空 false：非空
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        checkIfNullItem(item);

        size++;
        Node<Item> newNode = new Node<>();
        newNode.item = item;
        if (first == null) {
            first = newNode;
            last = newNode;
        } else {
            newNode.next = first;
            first.prev = newNode;
            first = newNode;
        }
    }

    private void checkIfNullItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
    }

    public void addLast(Item item) {
        checkIfNullItem(item);

        size++;
        Node<Item> newNode = new Node<>();
        newNode.item = item;
        if (first == null) {
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }
    }

    public Item removeFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }

        Item item = first.item;
        first.item = null;
        if (size > 1) {
            first = first.next;
            first.prev = null;
        } else {
            first = null;
            last = null;
        }

        size--;
        return item;
    }

    public Item removeLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }

        Item item = last.item;
        last.item = null;
        if (size > 1) {
            last = last.prev;
            last.next = null;
        } else {
            first = null;
            last = null;
        }
        size--;
        return item;
    }

    // return an iterator over items in order from front to back
    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node<Item> cur;
        public DequeIterator() {
            this.cur = first;
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public Item next() {
            if (cur == null) {
                throw new NoSuchElementException();
            }

            Item item = cur.item;
            cur = cur.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class Node<Item> {
        private Item item;

        private Node<Item> next;
        private Node<Item> prev;
    }

}