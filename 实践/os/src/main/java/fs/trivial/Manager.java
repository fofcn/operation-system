package fs.trivial;

/**
 * manager interface
 *
 * @author jiquanxi
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
