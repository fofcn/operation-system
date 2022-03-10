package distributed.election.bully.state;


/**
 * 角色定义
 * 四种角色：
 * Coordinator（协调者，我是老大都听我的）、
 * Follower（追随者，我们都挺老大的）、
 * Participant(参与者，投票哪)、
 * Non-Participant（非参与者，刚开始我啥也不是，我得赶紧加入组织）
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public enum RoleEnum {
    COORDINATOR(1, "协调者"),
    FOLLOWER(2, "追随者"),
    PARTICIPANT(3, "参与者"),
    NON_PARTICIPANT(4, "非参与者");

    private int code;
    private String desc;

    RoleEnum(int code, String desc) {
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
