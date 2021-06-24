package org.truenewx.tnxjee.core.beans;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.concurrent.ExecutorUtil;

/**
 * 在容器初始化完成后延时执行操作的bean
 *
 * @author jianglei
 */
public abstract class DelayContextInitializedBean implements ContextInitializedBean {

    /**
     * 默认的最小延时执行毫秒数
     */
    public static final long DEFAULT_MIN_DELAY_MILLIS = 3000;

    private ScheduledExecutorService executor = ExecutorUtil.buildScheduledExecutor(getCorePoolSize());

    protected int getCorePoolSize() {
        return 4;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Class<?> beanClass = getClass();
        this.executor.schedule(() -> {
            try {
                execute();
            } catch (Exception e) {
                LogUtil.error(beanClass, e);
            }
        }, getDelayMillis(), TimeUnit.MILLISECONDS);
    }

    protected long getDelayMillis() {
        // 默认延时启动毫秒数为[最小延时,两倍最小延时]之间的随机数，以尽量错开初始化执行的启动时间
        return MathUtil.randomLong(DEFAULT_MIN_DELAY_MILLIS, DEFAULT_MIN_DELAY_MILLIS * 2);
    }

    protected abstract void execute();

}
