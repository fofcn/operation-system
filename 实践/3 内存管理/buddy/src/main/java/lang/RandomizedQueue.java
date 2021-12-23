package lang;/* *****************************************************************************
 *  Name: 冀全喜
 *  email: errorfatal89@gmail.com
 *  Date: 2021.11.05
 *  Description:
 **************************************************************************** */

import util.StdOut;
import util.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int index = 0;

    private static final int DEFAULT_CAPACITY = 2;

    private Item[] data;

    // construct an empty randomized queue
    public RandomizedQueue() {
        data = (Item[]) new Object[DEFAULT_CAPACITY];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return index == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return index;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (data.length == index) {
            resize(2 * data.length);
        }

        data[index++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (index == 0) {
            throw new NoSuchElementException();
        }

        int rndIdx = StdRandom.uniform(index);
        Item item = data[rndIdx];
        data[rndIdx] = null;
        if (rndIdx != index - 1) {
            data[rndIdx] = data[index - 1];
        }

        if (index == data.length / 4) {
            resize(data.length / 2);
        }

        index--;

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (index == 0) {
            throw new NoSuchElementException();
        }
        int rndIdx = StdRandom.uniform(index);
        return data[rndIdx];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIteraor();
    }

    private class RandomizedQueueIteraor implements Iterator<Item> {
        private Item[] copy;
        private int idx = index;

        public RandomizedQueueIteraor() {
            if (index == 0) {
                return;
            }

            copy = (Item[]) new Object[index];
            for (int i = 0; i < index; i++) {
                copy[i] = data[i];
            }
        }

        public boolean hasNext() {
            return idx != 0;
        }

        public Item next() {
            if (idx == 0) {
                throw new NoSuchElementException();
            }
            int rndIdx = StdRandom.uniform(idx);
            Item item = copy[rndIdx];
            copy[rndIdx] = null;
            if (rndIdx != idx - 1) {
                copy[rndIdx] = copy[idx - 1];
            }

            idx--;

            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private void resize(int newSize) {
        Item[] newData = (Item[]) new Object[newSize];
        for (int i = 0; i < index; i++) {
            newData[i] = data[i];
        }

        data = newData;
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        boolean empty = queue.isEmpty();
        StdOut.println("should be empty: " + empty);
        StdOut.println("should be 0: " + queue.size());

        try {
            queue.enqueue(null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        try {
            queue.dequeue();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 1000; i++) {
            queue.enqueue(i);
        }

        StdOut.println("should not be empty: " + queue.isEmpty());
        StdOut.println("should be 1000: " + queue.size());

        int item = queue.dequeue();
        StdOut.println("random select item : " + item);
        StdOut.println("should be 999: " + queue.size());

        item = queue.sample();
        StdOut.println("sample item : " + item);
        StdOut.println("should be 999: " + queue.size());

        Iterator<Integer> iterator = queue.iterator();
        while (iterator.hasNext()) {
            StdOut.print(iterator.next() + " ");
        }
        StdOut.println();
    }

}
