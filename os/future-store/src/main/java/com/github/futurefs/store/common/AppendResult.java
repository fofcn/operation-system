package com.github.futurefs.store.common;

import com.github.futurefs.common.ResultCode;
import lombok.Data;

/**
 * 追加写结果
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/24 14:03
 */
@Data
public class AppendResult {
    /**
     * 结果
     */
    private int result;

    /**
     * 写入起始偏移地址
     */
    private long offset;

    public AppendResult() {
        this(ResultCode.SUCCESS);
    }

    public AppendResult(int result) {
        this.result = result;
    }
}
