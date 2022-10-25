package org.truenewx.tnxjee.core.util.concurrent;

/**
 * 可获取进度的任务
 *
 * @param <P> 进度类型
 */
public interface ProgressTask<P extends TaskProgress<?>> extends Runnable {

    P getProgress();

}
