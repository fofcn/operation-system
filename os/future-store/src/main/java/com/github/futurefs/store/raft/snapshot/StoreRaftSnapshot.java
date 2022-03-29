package com.github.futurefs.store.raft.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/29 16:51
 */
@Slf4j
public class StoreRaftSnapshot {

    private String path;

    public StoreRaftSnapshot(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
    /**
     * Save value to snapshot file.
     */
    public boolean save(final long value) {
        try {
            FileUtils.writeStringToFile(new File(path), String.valueOf(value));
            return true;
        } catch (IOException e) {
            log.error("Fail to save snapshot", e);
            return false;
        }
    }

    public long load() throws IOException {
        final String s = FileUtils.readFileToString(new File(path));
        if (!StringUtils.isBlank(s)) {
            return Long.parseLong(s);
        }
        throw new IOException("Fail to load snapshot from " + path + ",content: " + s);
    }
}
