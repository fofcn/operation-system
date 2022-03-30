package com.github.futurefs.store.distributed.raft;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.error.RaftException;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.alipay.sofa.jraft.util.Utils;
import com.github.futurefs.store.distributed.raft.snapshot.StoreRaftSnapshot;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29
 */
@Slf4j
public class StoreRaftStateMachine extends StateMachineAdapter {
    /**
     * Leader term
     */
    private final AtomicLong leaderTerm = new AtomicLong(-1);

    /**
     * offset value
     */
    private final AtomicLong offset = new AtomicLong(0);

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    public long getOffset() {
        return this.offset.get();
    }


    @Override
    public void onApply(final Iterator iter) {
        while (iter.hasNext()) {
            long current = 0;
            StoreRaftOperation storeR = null;

            StoreRaftClosure closure = null;
            if (iter.done() != null) {
                // This task is applied by this node, get value from closure to avoid additional parsing.
                closure = (StoreRaftClosure) iter.done();
                storeR = closure.getCounterOperation();
            } else {
                // Have to parse FetchAddRequest from this user log.
                final ByteBuffer data = iter.getData();
                try {
                    storeR = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(
                            data.array(), StoreRaftOperation.class.getName());
                } catch (final CodecException e) {
                    log.error("Fail to decode IncrementAndGetRequest", e);
                }
            }
            if (storeR != null) {
                log.info("Added offset={}  at logIndex={}", storeR.getWritePos(), iter.getIndex());
                if (closure != null) {
                    closure.success(storeR.getWritePos());
                    closure.run(Status.OK());
                }
            }
            iter.next();
        }
    }

    @Override
    public void onSnapshotSave(final SnapshotWriter writer, final Closure done) {
        final long currVal = this.offset.get();
        Utils.runInThread(() -> {
            final StoreRaftSnapshot snapshot = new StoreRaftSnapshot(writer.getPath() + File.separator + "data");
            if (snapshot.save(currVal)) {
                if (writer.addFile("data")) {
                    done.run(Status.OK());
                } else {
                    done.run(new Status(RaftError.EIO, "Fail to add file to writer"));
                }
            } else {
                done.run(new Status(RaftError.EIO, "Fail to save counter snapshot %s", snapshot.getPath()));
            }
        });
    }

    @Override
    public void onError(final RaftException e) {
        log.error("Raft error: {}", e, e);
    }

    @Override
    public boolean onSnapshotLoad(final SnapshotReader reader) {
        if (isLeader()) {
            log.warn("Leader is not supposed to load snapshot");
            return false;
        }
        if (reader.getFileMeta("data") == null) {
            log.error("Fail to find data file in {}", reader.getPath());
            return false;
        }
        final StoreRaftSnapshot snapshot = new StoreRaftSnapshot(reader.getPath() + File.separator + "data");
        try {
            this.offset.set(snapshot.load());
            return true;
        } catch (final IOException e) {
            log.error("Fail to load snapshot from {}", snapshot.getPath());
            return false;
        }

    }

    @Override
    public void onLeaderStart(final long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);

    }

    @Override
    public void onLeaderStop(final Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

}
