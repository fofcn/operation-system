package memory.repalcement;

import java.util.Arrays;

/**
 * Least recently used algorithm.
 * 最近最少使用算法
 * 算法思想： 选择最后一次访问时间距离当前时间最长的一页并置换
 * @author errorfatal89@gmail.com
 * @date 2021/12/23
 */
public class LruAlg<T extends Comparable<T>> implements ReplacementAlg<T> {

    private final int maxCapacity;
    private final Node<T>[] keys;
    private int index = 0;


    public LruAlg(final int maxCapacity) {
        this.maxCapacity = maxCapacity;
        keys =  new Node[maxCapacity];
    }

    @Override
    public void set(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("obj");
        }

        Node<T> node = new Node(obj);

        // 缓存即将溢出，需要剔除最早使用的节点
        if (index >= maxCapacity) {
            // 覆盖第一个
            index = 0;
        }
        // 所有插入都写入到数组尾部
        //
        keys[index++] = node;


    }

    @Override
    public T get(T key) {
        for (Node node : keys) {
            if (node.getKey().equals(key)) {
                // 更新访问时间
                node.setTimestamp(System.currentTimeMillis());
                return (T) node.getKey();
            }
        }

        Arrays.sort(keys);
        return null;
    }

    public class Node<T> implements Comparable<Node> {
        private final T key;
        private long timestamp = System.currentTimeMillis();

        public Node(T key) {
            this.key = key;
        }

        @Override
        public int compareTo(Node o) {
            return timestamp - o.getTimestamp() > 0 ? 0 :
                    timestamp - o.getTimestamp() < 0 ? -1 : 0;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public T getKey() {
            return key;
        }
    }
}
