package cache;

/**
 * 缓存节点
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/07
 */
public class CacheNode<K, V> {
    protected volatile K key;

    protected volatile V value;

    private volatile CacheNode<K, V> prev;

    private volatile CacheNode<K, V> next;

    public CacheNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public CacheNode<K, V> getPrev() {
        return prev;
    }

    public void setPrev(CacheNode<K, V> prev) {
        this.prev = prev;
    }

    public CacheNode<K, V> getNext() {
        return next;
    }

    public void setNext(CacheNode<K, V> next) {
        this.next = next;
    }

}
