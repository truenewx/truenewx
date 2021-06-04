package org.truenewx.tnxjee.core.util.concurrent;

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

    public static ScheduledThreadPoolExecutor buildScheduledExecutor(int corePoolSize) {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern(ScheduledExecutorService.class.getSimpleName() + "-%d")
                .daemon(true).build();
        return new ScheduledThreadPoolExecutor(corePoolSize, factory);
    }

}
