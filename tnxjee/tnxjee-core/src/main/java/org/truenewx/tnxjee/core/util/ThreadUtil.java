package org.truenewx.tnxjee.core.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

/**
 * 线程工具类
 *
 * @author jianglei
 */
public class ThreadUtil {

    private ThreadUtil() {
    }

    /**
     * 当前线程睡眠
     *
     * @param interval 睡眠间隔，超出则唤醒
     */
    public static void sleep(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * 当前线程睡眠，直到超时或条件满足
     *
     * @param interval 睡眠间隔，单位：毫秒
     * @param timeout  超时时间：单位：毫秒
     * @param until    唤醒条件，为null时忽略
     * @return 是否因满足指定条件而唤醒，false-因超时而唤醒
     */
    public static boolean sleep(long interval, long timeout, BooleanSupplier until) {
        if (until != null && until.getAsBoolean()) {
            return true;
        }
        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < timeout) {
            sleep(interval);
            if (until != null && until.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    public static void runAsync(Runnable runnable, Executor executor) {
        if (executor == null) {
            CompletableFuture.supplyAsync(() -> {
                runnable.run();
                return null;
            });
        } else {
            CompletableFuture.supplyAsync(() -> {
                runnable.run();
                return null;
            }, executor);
        }
    }

    public static void runAsync(Runnable runnable) {
        runAsync(runnable, null);
    }

}
