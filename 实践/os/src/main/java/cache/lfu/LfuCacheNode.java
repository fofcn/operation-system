package cache.lfu;

import cache.CacheNode;

/**
 * LFU缓存节点
 *
 * @author jiquanxi
 * @date 2022/01/10
 */
public class LfuCacheNode<K, V> extends CacheNode<K, V> {

    private volatile int frequency;

    public LfuCacheNode(K key, V value) {
        this(key, value, 1);
    }

    public LfuCacheNode(K key, V value, int frequency) {
        super(key, value);
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "LfuCacheNode{" +
                "frequency=" + frequency +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
