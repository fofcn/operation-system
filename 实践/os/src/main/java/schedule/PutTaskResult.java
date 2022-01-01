package schedule;

/**
 * 任务入队结果
 */
public class PutTaskResult {

    /**
     * 是否入队成功
     */
    private boolean isEnqueued;

    /**
     * 是否现在必须执行
     */
    private boolean isMustRunNow;

    public boolean isEnqueued() {
        return isEnqueued;
    }

    public void setEnqueued(boolean enqueued) {
        isEnqueued = enqueued;
    }

    public boolean isMustRunNow() {
        return isMustRunNow;
    }

    public void setMustRunNow(boolean mustRunNow) {
        isMustRunNow = mustRunNow;
    }
}
