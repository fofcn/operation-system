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
     * @return true:成功，false:失败
     */
    boolean victory(int identifier);

    /**
     * 广播选举
     */
    void broadcastElection();

    /**
     * 同步发送心跳消息
     * @return true:成功，false:失败
     */
    boolean sendHeartBeat();

    /**
     * 获取自己的选举标识符
     * @return 选举标识符
     */
    int getIdentifier();

    /**
     * 其他协调者当选
     * @param identifier 选举标识符
     */
    void coordinatorVictory(int identifier);

    /**
     * 获取当前协调者标识符
     * @return 当前协调者标识符
     */
    int getCoordinatorId();


}
