package lang;/* *****************************************************************************
 *  Name: 冀全喜
 *  email: errorfatal89@gmail.com
 *  Date: 2021.11.05
 *  Description:
 **************************************************************************** */

public class Stack<Item> {

    private Node first = null;

    private class Node {
        Item item;

        Node next;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public void push(Item item) {
        Node newNode = new Node();
        newNode.item = item;

        newNode.next = first;
        first = newNode;
    }

    public Item pop() {
        Node node = first;
        first = first.next;
        return node.item;
    }

    public static void main(String[] args) {
        Stack<Integer> s = new Stack<>();
        s.push(17);
        int a = s.pop();
        System.out.println(a);
    }
}