package distributed.network.enums;

/**
 * 消息类型
 * @author errorfatal89@gmail.com
 */
public enum RequestCode {
    ELECTION(1, "选举消息"),
    ELECTION_ACK(2, "选举消息应答"),
    VICTORY(3, "获胜消息"),
    ;

    private int code;

    private String desc;

    RequestCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
