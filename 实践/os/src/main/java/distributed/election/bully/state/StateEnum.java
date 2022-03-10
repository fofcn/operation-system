package distributed.election.bully.state;

/**
 * 状态定义
 * 1、初始化状态（刚启动，啥都不是）
 * 2、选举中（发起了一次选举）
 * 3、选举完成（自己或者别人当了协调者）
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public enum StateEnum {
    INITIALIZATION(1, "初始化"),
    ELECTING(2, "选举中"),
    ELECTION_DONE(3, "选举完成");

    private int code;
    private String desc;

    StateEnum(int code, String desc) {
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
