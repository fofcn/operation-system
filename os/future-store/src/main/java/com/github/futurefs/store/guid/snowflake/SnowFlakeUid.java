package com.github.futurefs.store.guid.snowflake;

import com.github.futurefs.store.guid.UidConfig;
import com.github.futurefs.store.guid.UidGenerator;
import com.xfvape.uid.buffer.RejectedPutBufferHandler;
import com.xfvape.uid.buffer.RejectedTakeBufferHandler;
import com.xfvape.uid.buffer.RingBuffer;
import com.xfvape.uid.impl.CachedUidGenerator;
import com.xfvape.uid.worker.WorkerIdAssigner;
import lombok.extern.slf4j.Slf4j;

/**
 * 雪花Uid
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/07 14:20
 */
@Slf4j
public class SnowFlakeUid implements UidGenerator<Long> {
    private final CachedUidGenerator cachedUidGenerator;

    private final SnowFlakeConfig config;

    public SnowFlakeUid(SnowFlakeConfig config) {
        this.config = config;
        this.cachedUidGenerator = new CachedUidGenerator();
        configGenerator();
    }

    @Override
    public Long generate() {
        return cachedUidGenerator.getUID();
    }

    private void configGenerator() {
        cachedUidGenerator.setBoostPower(config.getBoostPower());
        cachedUidGenerator.setPaddingFactor(config.getPaddingFactor());
        cachedUidGenerator.setScheduleInterval(config.getScheduleInterval());
        cachedUidGenerator.setEpochStr(config.getEpochStr());
        cachedUidGenerator.setSeqBits(config.getSeqBits());
        cachedUidGenerator.setWorkerBits(config.getWorkerBits());
        cachedUidGenerator.setTimeBits(config.getTimeBits());
        cachedUidGenerator.setWorkerIdAssigner(new CustomIdAssigner());
        cachedUidGenerator.setRejectedPutBufferHandler(new CustomRejectPutHandler());
        cachedUidGenerator.setRejectedTakeBufferHandler(new CustomRejectTakeHandler());
    }

    class CustomIdAssigner implements WorkerIdAssigner {
        @Override
        public long assignWorkerId() {
            return config.getWorkerId();
        }
    }

    class CustomRejectPutHandler implements RejectedPutBufferHandler {

        @Override
        public void rejectPutBuffer(RingBuffer ringBuffer, long uid) {
            log.warn("reject put buffer, ringBuffer: <{}>", ringBuffer);
        }
    }

    class CustomRejectTakeHandler implements RejectedTakeBufferHandler {

        @Override
        public void rejectTakeBuffer(RingBuffer ringBuffer) {
            log.warn("reject take buffer, ringBuffer: <{}>", ringBuffer);
        }
    }
}


