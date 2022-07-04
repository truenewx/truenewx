package org.truenewx.tnxjee.core.util.concurrent;

import java.io.Serializable;
import java.util.Objects;

/**
 * 任务进度
 */
public class TaskProgress<K extends Serializable> {

    private K id;
    private Long startTime;
    private Long completeTime;

    public TaskProgress(K id) {
        this.id = id;
    }

    public K getId() {
        return this.id;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Long getCompleteTime() {
        return this.completeTime;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isStarted() {
        return this.startTime != null;
    }

    public boolean isRunning() {
        return isStarted() && !isCompleted();
    }

    public void complete() {
        this.completeTime = System.currentTimeMillis();
    }

    public boolean isCompleted() {
        return this.completeTime != null;
    }

    /**
     * @return 耗时毫秒数
     */
    public long getConsumedMilliseconds() {
        if (this.startTime != null) {
            long endTime = Objects.requireNonNullElseGet(this.completeTime, System::currentTimeMillis);
            return endTime - this.startTime;
        }
        return 0;
    }

}
