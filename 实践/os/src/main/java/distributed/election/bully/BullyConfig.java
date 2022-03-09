package distributed.election.bully;

/**
 * Bully 配置
 *
 * @author jiquanxi
 * @date 2022/03/09
 */
public class BullyConfig {

    /**
     * 集群IP地址和端口
     * 配置方式(特别注意：标识符配置不能重复)：
     * 单节点：标识符+冒号+IP+英文冒号+端口，例如：1:127.0.0.1:60000
     * 多节点：以逗号分割单节点，例如：1:127.0.0.1:60000;2:127.0.0.1:60001
     */
    private String clusterNodes;

    /**
     * 自己的节点信息,配置同集群单节点相同
     */
    private String nodeInfo;
}
