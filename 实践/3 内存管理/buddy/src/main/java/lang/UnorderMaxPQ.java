package lang;/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class UnorderMaxPQ<Key extends Comparable<Key>> {

    private Key[] pq;
    private int n;

    public UnorderMaxPQ(int capaciy) {
        pq = (Key[]) new Comparable[capaciy];
    }

    // theta(1)
    public boolean isEmpty() {
        return n == 0;
    }

    // theta(1)
    public void insert(Key x) {
        pq[n++] = x;
    }

    // 最好情况 theta(n)
    // 最坏情况 theta(n)
    // 平均情况 theta(n)
    public Key delMax() {
        int max = 0;
        for (int i = 1; i < n; i++) { // n
            if (less(max, i)) { // n
                max = i; // n
            }
        }

        exch(max, n - 1); // n
        return pq[--n];
    }

    private void exch(int max, int i) {
    }

    private boolean less(int max, int i) {
        return false;
    }

    public static void main(String[] args) {

    }
}
