package org.truenewx.tnxjee.core.util.concurrent;

/**
 * 默认的可获取进度的任务
 *
 * @param <P> 进度类型
 */
public abstract class DefaultProgressTask<P extends TaskProgress<?>> implements ProgressTask<P> {

    protected P progress;

    public DefaultProgressTask(P progress) {
        this.progress = progress;
    }

    @Override
    public P getProgress() {
        return this.progress;
    }

}
