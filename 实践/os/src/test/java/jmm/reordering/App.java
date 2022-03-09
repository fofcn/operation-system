package jmm.reordering;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/12
 * @see <a href="https://stackoverflow.com/questions/52648800/how-to-demonstrate-java-instruction-reordering-problems"></>
 */
public class App {

    public static void main(String[] args) {

        for (int i = 0; i < 1000_000; i++) {
            final State state = new State();

            // a = 0, b = 0, c = 0

            // Write values
            new Thread(() -> {
                state.a = 1;
                // a = 1, b = 0, c = 0
                state.b = 1;
                // a = 1, b = 1, c = 0
                state.c = state.a + 1;
                // a = 1, b = 1, c = 2
            }).start();

            // Read values - this should never happen, right?
            new Thread(() -> {
                // copy in reverse order so if we see some invalid state we know this is caused by reordering and not by a race condition in reads/writes
                // we don't know if the reordered statements are the writes or reads (we will se it is writes later)
                int tmpC = state.c;
                int tmpB = state.b;
                int tmpA = state.a;

                if (tmpB == 1 && tmpA == 0) {
                    System.out.println("Hey wtf!! b == 1 && a == 0");
                }
                if (tmpC == 2 && tmpB == 0) {
                    System.out.println("Hey wtf!! c == 2 && b == 0");
                }
                if (tmpC == 2 && tmpA == 0) {
                    System.out.println("Hey wtf!! c == 2 && a == 0");
                }
            }).start();

        }
        System.out.println("done");
    }

    static class State {
        int a = 0;
        int b = 0;
        int c = 0;
    }

}
