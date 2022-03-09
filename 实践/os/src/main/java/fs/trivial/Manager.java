package fs.trivial;

/**
 * manager interface
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/29
 */
public interface Manager {

    /**
     * initialize
     * @return
     */
    boolean initialize();

    /**
     * start
     * @return
     */
    void start();

    /**
     * shutdown
     * @return
     */
    void shutdown();
}
