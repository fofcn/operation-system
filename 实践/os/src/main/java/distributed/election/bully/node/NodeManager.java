package distributed.election.bully.node;

/**
 * 节点管理器
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public interface NodeManager {

    /**
     * 初始化方法
     * @return true:成功；false:失败
     */
    boolean initialize();

    /**
     * 启动节点管理器
     */
    void start();

    /**
     * 停止节点管理器
     */
    void shutdown();

    /**
     * 获取当前节点的角色
     * @return
     */
    int getRole();

    /**
     * 获取集群节点数量
     * @return 集群节点数量
     */
    int getNodeSize();

    /**
     * 某个节点获胜
     * @param identifier 选举标识符
     */
    void victory(int identifier);

    /**
     * 广播选举
     */
    void broadcastElection();
}
