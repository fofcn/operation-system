package lang;/* *****************************************************************************
 *  Name: errorfatal89@gmail.com
 *  Date: 2021.11.25
 *  Description: 最大堆
 **************************************************************************** */

public class MaxPQ<Key extends Comparable<Key>> {
    private Key[] pq;
    private int n;

    public MaxPQ(int capacity) {
        pq = (Key[]) new Comparable[capacity + 1];
    }

    /**
     * 向堆中插入键
     * 算法：
     * 将数据直接插入到堆末尾，然后调整元素的位置
     *
     * 运行时间分析：
     * @param v
     * swim lg(n)
     */
    public void insert(Key v) {
        pq[++n] = v;

        // 调整位置
        swim(n);
    }

    /**
     * 删除最大的元素
     * 根据堆的特性，第一个元素是最大的，取出第一个元素
     * 然后将最后一个元素放到第一个元素位置。
     * 我们将第一个元素进行降级操作
     * 最后将元素的最后一个元素设置为空释放内存
     * @return
     *
     * 运行时间分析： lg(n)
     */
    public Key delMax() {
        Key max = pq[1];
        exch(1, n--);
        sink(1);
        pq[n+1] = null;
        return max;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public Key max() {
        return pq[1];
    }

    public int size() {
        return n;
    }

    private void swim(int k) {
        // 如果插入位置是1，那么只有一个元素，不用处理上升
        // 如果插入元素小于他的父级节点，不用处理上升
        while (k > 1 && less(k / 2, k)) {
            exch(k / 2, k);
            k = k / 2;
        }
    }

    private void sink(int k) {
        // 根据k查找子节点
        // 2k = 左边子节点
        // 2k+1=右子节点
        while (k * 2 <= n) {
            int j = 2 * k;
            if (j < n && less(j, j + 1)) {
                j++;
            }

            if (!less(k, j)) {
                break;
            }

            exch(k, j);
            k = j;
        }
    }

    private boolean less(int i, int j) {
        return pq[i].compareTo(pq[j]) < 0;
    }

    private void exch(int i, int j) {
        Key t = pq[i];
        pq[i] = pq[j];
        pq[j] = t;
    }
}
