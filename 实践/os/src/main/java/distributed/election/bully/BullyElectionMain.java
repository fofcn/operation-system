package distributed.election.bully;

import distributed.election.bully.config.BullyConfig;
import distributed.network.config.NettyClientConfig;
import distributed.network.config.NettyServerConfig;
import distributed.util.YamlUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 霸道选举算法启动类
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
@Slf4j
public class BullyElectionMain {

    public void start() {
        BullyConfig bullyConfig = new BullyConfig();


    }
    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("请传入配置文件名称");
            return;
        }

        BullyConfig bullyConfig = YamlUtil.readObject(BullyConfig.class, args[0]);
        NettyServerConfig serverConfig = YamlUtil.readObject(NettyServerConfig.class, args[0]);
        NettyClientConfig clientConfig = YamlUtil.readObject(NettyClientConfig.class, args[0]);
        bullyConfig.setNettyServerConfig(serverConfig);
        bullyConfig.setNettyClientConfig(clientConfig);
        BullyElectionAlgorithm bullyElectionAlgorithm = new BullyElectionAlgorithm(bullyConfig);
        bullyElectionAlgorithm.init();
        bullyElectionAlgorithm.start();
    }
}
