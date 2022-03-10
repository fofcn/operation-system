package distributed.election.bully.config;

/**
 * 霸道选举算法节点配置
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public class BullyNodeConfig {
    /**
     * 选举标识符
     */
    private int identifier;

    /**
     * 节点IP
     */
    private String ip;

    /**
     * 节点端口
     */
    private int port;

    /**
     * 是否节点是自己
     */
    private boolean self;

    /**
     * 地址， ip:port
     */
    private String address;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
