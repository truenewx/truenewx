package org.truenewx.tnxjee.core.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 线程池工具类
 *
 * @author jianglei
 */
public class ExecutorUtil {

    private ExecutorUtil() {
    }

    public static final String DEFAULT_EXECUTOR_BEAN_NAME = "defaultExecutor";
    public static final String SCHEDULED_EXECUTOR_BEAN_NAME = "scheduledExecutor";

    public static ExecutorService buildDefaultExecutor(int corePoolSize) {
        return new DefaultThreadPoolExecutor(corePoolSize);
    }

    public static ScheduledExecutorService buildScheduledExecutor(int corePoolSize) {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern(ScheduledExecutorService.class.getSimpleName() + "-%d")
                .daemon(true).build();
        return new ScheduledThreadPoolExecutor(corePoolSize, factory);
    }

}
