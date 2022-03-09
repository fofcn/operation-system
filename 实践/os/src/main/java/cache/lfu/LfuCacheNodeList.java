package cache.lfu;

import cache.CacheNode;
import cache.LinkedList;

/**
 *
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/11
 */
public class LfuCacheNodeList<K, V> {

    private final int frequency;

    private volatile LinkedList list;

    private volatile LfuCacheNodeList prev;

    private volatile LfuCacheNodeList next;

    public LfuCacheNodeList(int frequency) {
        this.frequency = frequency;
        this.list = new LinkedList();
    }

    public LfuCacheNode<K, V> addLast(LfuCacheNode<K, V> node) {
        list.addLast(node);
        return node;
    }

    public int getFrequency() {
        return frequency;
    }

    public LfuCacheNodeList getPrev() {
        return prev;
    }

    public void setPrev(LfuCacheNodeList prev) {
        this.prev = prev;
    }

    public LfuCacheNodeList getNext() {
        return next;
    }

    public void setNext(LfuCacheNodeList next) {
        this.next = next;
    }

    public int size() {
        return list.getSize();
    }

    public <V, K> void remove(LfuCacheNode<K,V> node) {
        list.remove(node);
    }

    public CacheNode removeFirst() {
        return list.removeFirst();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("LfuCacheNodeList{" +
                "frequency=" + frequency +
                '}');
        for (Object node : list) {
            builder.append(node);
        }

        return builder.toString();
    }
}
