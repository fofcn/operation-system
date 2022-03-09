package com.fofcn.mm.lang;

import lang.Deque;
import org.junit.Test;
import util.StdOut;

import java.util.Iterator;

/**
 * 双端队列测试
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/23
 */
public class DequeTest {

    @Test
    // unit testing (required)
    public void main() {
        // empty deque add test
        Deque<Integer> deque = new Deque<>();
        // isEmpty test
        StdOut.println("empty deque: " + deque.isEmpty());
        StdOut.println("empty deque: " + deque.size());
        deque.addFirst(1);
        StdOut.println("empty deque add first test.");

        Integer item;
        deque = new Deque<>();
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addLast(3);
        deque.addLast(4);
        while ((item = deque.removeFirst()) != null) {
            StdOut.print(item + " ");
        }
        StdOut.println();

        deque = new Deque<>();
        deque.addLast( 1);
        StdOut.println("empty deque add last test.");

        // empty deque add last multi-item test
        deque = newDequeAddFirst(7);
        StdOut.println("assert: after add 7 items, expect deque.size() == 7, actual result: " + (deque.size() == 7));
        StdOut.println("assert: after add 7 items, expect deque.isEmpty() == false, actual result: " + deque.isEmpty());
        // test remove first

        while ((item = deque.removeFirst()) != null) {
            StdOut.print(item + " ");
        }
        StdOut.println();
        deque = newDequeAddFirst(7);
        StdOut.println("assert: after add 7 items, expect deque.size() == 7, actual result: " + (deque.size() == 7));
        StdOut.println("assert: after add 7 items, expect deque.isEmpty() == false, actual result: " + deque.isEmpty());
        // test remove first
        while ((item = deque.removeLast()) != null) {
            StdOut.print(item + " ");
        }
        StdOut.println();

        // empty deque add first multi-item test
        deque = newDequeAddLast(7);

        StdOut.println("assert: after add 7 items, expect deque.size() == 7, actual result: " + (deque.size() == 7));
        StdOut.println("assert: after add 7 items, expect deque.isEmpty() == false, actual result: " + deque.isEmpty());

        // test remove first
        while ((item = deque.removeFirst()) != null) {
            StdOut.print(item + " ");
        }
        StdOut.println();

        // Iterator test with 12 items
        deque = newDequeAddLast(12);
        Iterator<Integer> iterator = deque.iterator();
        while (iterator.hasNext()) {
            StdOut.print(iterator.next() + " ");
        }
        StdOut.println();

        deque = new Deque<Integer>();
        iterator = deque.iterator();
        while (iterator.hasNext()) {
            StdOut.print(iterator.next());
        }
        StdOut.println();

        try {
            iterator.remove();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }


    }

    private static Deque<Integer> newDequeAddFirst(int size) {
        // empty deque add first multi-item test
        Deque<Integer> deque = new Deque<>();
        for (int i = 0; i < size; i++) {
            deque.addFirst( i + 1);
        }

        return deque;
    }

    private static Deque<Integer> newDequeAddLast(int size) {
        // empty deque add first multi-item test
        Deque<Integer> deque = new Deque<>();
        for (int i = 0; i < size; i++) {
            deque.addLast( i + 1);
        }

        return deque;
    }
}
