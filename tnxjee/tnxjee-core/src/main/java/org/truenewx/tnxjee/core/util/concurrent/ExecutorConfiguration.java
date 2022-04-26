package org.truenewx.tnxjee.core.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ExecutorConfiguration {

    @Primary
    @Bean(name = ExecutorUtil.DEFAULT_EXECUTOR_BEAN_NAME, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = ExecutorUtil.DEFAULT_EXECUTOR_BEAN_NAME)
    public ExecutorService defaultExecutor() {
        return ExecutorUtil.buildDefaultExecutor(getCorePoolSize());
    }

    protected int getCorePoolSize() {
        return 4;
    }

    @Bean(name = ExecutorUtil.SCHEDULED_EXECUTOR_BEAN_NAME, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = ExecutorUtil.SCHEDULED_EXECUTOR_BEAN_NAME)
    public ScheduledExecutorService scheduledExecutor() {
        return ExecutorUtil.buildScheduledExecutor(getCorePoolSize());
    }

}
