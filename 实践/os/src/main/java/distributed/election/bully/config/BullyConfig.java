package distributed.election.bully.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import distributed.network.config.NettyClientConfig;
import distributed.network.config.NettyServerConfig;
import lombok.Data;

/**
 * 霸道选举算法配置
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/09
 */
@Data
public class BullyConfig {

    /**
     * 集群IP地址和端口
     * 配置方式(特别注意：标识符配置不能重复)：
     * 单节点：标识符+冒号 + 是否为自己(1：自己，0：不是自己) + 冒号 + IP+英文冒号+端口，
     *  例如：1:1:127.0.0.1:60000
     * 多节点：以逗号分割单节点，例如：1:1:127.0.0.1:60000;2:1:127.0.0.1:60001
     */
    @JsonProperty("clusterNodes")
    private String clusterNodes;

    private NettyServerConfig nettyServerConfig;

    private NettyClientConfig nettyClientConfig;
}
