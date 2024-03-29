package org.truenewx.tnxjee.core.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.ExceptionUtil;

/**
 * 容器初始化后执行bean代理，为目标bean提供线程执行能力
 *
 * @author jianglei
 */
public class ContextInitializedBeanProxy extends DelayContextInitializedBean {

    private ContextInitializedBean target;
    private long delayMillis = DEFAULT_MIN_DELAY_MILLIS;

    public void setTarget(ContextInitializedBean target) {
        Assert.isTrue(!(target instanceof ContextInitializedBeanProxy),
                "The target can not be a ContextInitializedBeanProxy");
        this.target = target;
    }

    public ContextInitializedBean getTarget() {
        return this.target;
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    protected long getDelayMillis() {
        return this.delayMillis;
    }

    @Override
    protected void execute(ApplicationContext context) {
        try {
            this.target.afterInitialized(context);
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }
}
