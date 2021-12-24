package lang.map;

/**
 * 分离链表实现HashMap
 *
 * @author jiquanxi
 * @date 2021/12/24
 */
public class SeparateChainingHashMap<Key, Value> implements Map<Key, Value> {

    private final Node<Key, Value>[] buckets;

    private int mod;

    private int size = 0;

    public SeparateChainingHashMap(int mod) {
        buckets = new Node[mod];
        this.mod = mod;
    }

    @Override
    public void put(Key key, Value value) {
        Value val = get(key);
        if (val != null) {
            return;
        }

        size++;
        Node<Key, Value> node = new Node<>(key, value);
        int i = hash(key);
        if (buckets[i] == null) {
            buckets[i] = node;
        } else {
            Node<Key, Value> n = buckets[i];
            do {
                if (n.next == null) {
                    break;
                }
                n = n.next;
            } while(true);

            n.next = node;
        }
    }

    @Override
    public Value get(Key key) {
        int i = hash(key);
        for (Node<Key, Value> n = buckets[i]; n != null; n = n.next) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private int hash(Key key) {
        if (key == null) {
            return 0;
        }
        return key.hashCode() & 0x7fffffff % mod;
    }

    private class Node<Key, Value> {
        private Key key;

        private Value value;

        private Node next;

        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }
}
