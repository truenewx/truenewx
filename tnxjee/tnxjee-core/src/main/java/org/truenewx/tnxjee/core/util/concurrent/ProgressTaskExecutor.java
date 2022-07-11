package org.truenewx.tnxjee.core.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 进度任务执行器
 *
 * @param <P> 进度类型
 */
@EnableScheduling
public class ProgressTaskExecutor<P extends TaskProgress<K>, K extends Serializable> {

    private ExecutorService executor;
    private Map<K, P> progresses = new Hashtable<>();

    public ProgressTaskExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public K submit(ProgressTask<P> task) {
        P progress = task.getProgress();
        K progressId = progress.getId();
        this.progresses.put(progressId, progress);
        this.executor.submit(task);
        return progressId;
    }

    public boolean isProgressing(K progressId) {
        return this.progresses.containsKey(progressId);
    }

    public P getProgress(K progressId) {
        return this.progresses.get(progressId);
    }

    public void remove(K progressId) {
        if (this.progresses.remove(progressId) != null) {
            LogUtil.info(getClass(), "====== The completed task progress(id={}) has been removed.", progressId);
        }
    }

    /**
     * 清理掉所有已完成的任务进度
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void clean() {
        Collection<P> progresses = this.progresses.values();
        for (P progress : progresses) {
            Long completeTime = progress.getCompleteTime();
            // 完成时间超过1分钟，则移除缓存的进度
            if (completeTime != null && System.currentTimeMillis() - completeTime > 60000) {
                remove(progress.getId());
            }
        }
    }

}
