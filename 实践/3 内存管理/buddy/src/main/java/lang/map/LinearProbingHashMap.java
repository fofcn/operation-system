package lang.map;

import org.omg.CORBA.Object;

/**
 * 线性探查Map
 *
 *
 * @author jiquanxi
 * @date 2021/12/24
 */
public class LinearProbingHashMap<Key, Value> implements Map<Key, Value> {
    private int size;
    private final int mod;
    private Key[] keys;
    private Value[] values;

    public LinearProbingHashMap(int mod) {
        this.mod = mod;
        this.keys = (Key[]) new Object[mod];
        this.values = (Value[]) new Object[mod];
    }

    @Override
    public void put(Key key, Value value) {
        int index;
        for (index = hash(key); keys[index] != null; index = (index + 1) % mod) {
            if (keys[index].equals(key)) {
                break;
            }
        }

        if (keys[index] == null) {
            size++;
        }
        keys[index] = key;
        values[index] = value;
    }

    @Override
    public Value get(Key key) {
        int index;
        for (index = hash(key); keys[index] != null; index = (index + 1) % mod) {
            if (keys[index].equals(key)) {
                return values[index];
            }
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private int hash(Key key) {
        return key.hashCode() & 0x7fffffff % mod;
    }
}
