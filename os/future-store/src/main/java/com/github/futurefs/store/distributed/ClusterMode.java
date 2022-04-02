package com.github.futurefs.store.distributed;

/**
 * 集群模式
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 13:25
 */
public enum ClusterMode {

    MASTER_SLAVE(1, "master slave模式"),
    INVALID_CLUSTER(-1, "无效集群模式");

    private int code;

    private String desc;

    ClusterMode(int code, String desc) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ClusterMode getByCode(int code) {
        for (int i = 0; i < ClusterMode.values().length; i++) {
            if (ClusterMode.values()[i].getCode() == code) {
                return ClusterMode.values()[i];
            }
        }

        return ClusterMode.INVALID_CLUSTER;
    }
}
