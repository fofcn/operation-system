package com.github.futurefs.store.guid.snowflake;

import com.github.futurefs.store.guid.UidConfig;
import lombok.Data;

/**
 * 雪花算法配置
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/07 14:26
 */
@Data
public class SnowFlakeConfig implements UidConfig {

    /**
     * worker id
     */
    private long workerId;

    /**
     * 时间的位数
     */
    private int timeBits = 30;

    /**
     * 集群worker的位数
     */
    private int workerBits = 27;

    /**
     * 自增序列号的位数
     */
    private int seqBits = 6;

    /**
     * 起始日期
     */
    private String epochStr = "2016-09-20";

    /**
     *  RingBuffer size扩容参数, 可提高UID生成的吞吐量.
     * 默认:3， 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536
     */
    private int boostPower;

    /**
     * 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50
     *  举例: bufferSize=1024, paddingFactor=50 -> threshold=1024 * 50 / 100 = 512.
     * 当环上可用UID数量 < 512时, 将自动对RingBuffer进行填充补全
     */
    private int paddingFactor;

    /**
     * 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
     * 默认:不配置此项, 即不实用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒
     */
    private int scheduleInterval;
}
