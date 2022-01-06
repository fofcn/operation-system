package memory.repalcement;

import lang.Deque;

/**
 * First-in-first-out algorithm 先进先出算法
 * 算法优点：
 * 1. 实现简单
 *
 * 算法缺点：
 * 1. 可能把最近经常使用的页面置换出去了，从而出现抖动的问题。
 * 例如：超市最先放到置物架的上售卖的产品一般都是家庭常用的，
 * 此时有新产品要上架，那么把家庭常用的产品下架将是一个错误的决定。
 * @author jiquanxi
 * @date 2021/12/23
 */
public class FifoAlg<T extends Comparable<T>> implements ReplacementAlg<T> {
    private final Deque<T> deque;
    private final int maxCapacity;

    public FifoAlg(final int maxCapacity) {
        deque = new Deque<>();
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void set(T obj) {
        if (deque.size() >= maxCapacity) {
            deque.removeLast();
        }

        deque.addFirst(obj);
    }

    @Override
    public T get(T key) {
        for (T t : deque) {
            if (t.equals(key)) {
                return t;
            }
        }

        return null;
    }
}
