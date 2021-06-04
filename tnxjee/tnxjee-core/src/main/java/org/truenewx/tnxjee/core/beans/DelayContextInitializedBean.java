package org.truenewx.tnxjee.core.beans;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.util.concurrent.ExecutorUtil;

/**
 * 在容器初始化完成后延时执行操作的bean
 *
 * @author jianglei
 */
public abstract class DelayContextInitializedBean implements ContextInitializedBean {

    public static final long DEFAULT_DELAY_MILLIS = 1500;

    private ScheduledExecutorService executor = ExecutorUtil.buildScheduledExecutor(getCorePoolSize());

    protected int getCorePoolSize() {
        return 2;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        this.executor.schedule(this::execute, getDelayMillis(), TimeUnit.MILLISECONDS);
    }

    protected long getDelayMillis() {
        return DEFAULT_DELAY_MILLIS;
    }

    protected abstract void execute();

}
