package com.github.futurefs.store.distributed.masterslave;

import com.github.futurefs.netty.BaseEnum;

/**
 * Master-Slave角色
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 13:36
 */
public enum MasterSlaveRole implements BaseEnum<MasterSlaveRole> {

    MASTER(1, "master"),
    SLAVE(2, "slave"),
    INVALID(2, "invalid");

    int code;

    String desc;

    MasterSlaveRole(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
