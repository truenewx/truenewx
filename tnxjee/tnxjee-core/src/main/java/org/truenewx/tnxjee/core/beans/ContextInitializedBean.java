package org.truenewx.tnxjee.core.beans;

import org.springframework.context.ApplicationContext;

/**
 * 在容器初始化完成后执行操作的bean
 *
 * @author jianglei
 */
public interface ContextInitializedBean {

    /**
     * 容器初始化完成后执行的动作
     *
     * @param context 容器上下文
     */
    void afterInitialized(ApplicationContext context) throws Exception;

}
