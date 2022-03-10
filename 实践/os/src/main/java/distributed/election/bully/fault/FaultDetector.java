package distributed.election.bully.fault;

/**
 * 故障检测器
 * 故障检测器负责：
 * 1. 检测协调者故障
 * 2. 发起选举
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public interface FaultDetector {

    /**
     * 启动故障检测
     */
    void start();

    /**
     * 停止故障检测
     */
    void shutdown();
}
