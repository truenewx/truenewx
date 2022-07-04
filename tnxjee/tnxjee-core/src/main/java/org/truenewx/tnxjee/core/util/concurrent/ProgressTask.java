package org.truenewx.tnxjee.core.util.concurrent;

/**
 * 可获取进度的任务
 *
 * @param <P> 进度类型
 */
public abstract class ProgressTask<P extends TaskProgress<?>> implements Runnable {

    protected P progress;

    public ProgressTask(P progress) {
        this.progress = progress;
    }

    public P getProgress() {
        return this.progress;
    }

}
