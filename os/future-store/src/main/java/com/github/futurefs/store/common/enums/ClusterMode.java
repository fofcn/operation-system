package com.github.futurefs.store.common.enums;

/**
 * 集群模式
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/31 14:25
 */
public enum ClusterMode {

    LEADER_FOLLOWER(1, "主从复制"),
    LEADER_FOLLOWER_LEARNER(2, "主从从复制"),
    LEADER_FOLLOWER_CANDIDATE(3, "一致性复制");

    private int code;

    private String desc;

    ClusterMode(int code, String desc) {
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
