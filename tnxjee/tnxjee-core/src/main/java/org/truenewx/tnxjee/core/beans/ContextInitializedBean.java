package org.truenewx.tnxjee.core.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * 在容器初始化完成后执行操作的bean
 *
 * @author jianglei
 */
public interface ContextInitializedBean extends Ordered {

    int DEFAULT_ORDER = 0;

    @Override
    default int getOrder() {
        return DEFAULT_ORDER;
    }

    /**
     * 容器初始化完成后执行的动作
     *
     * @param context 容器上下文
     * @throws Exception 如果处理过程出现错误
     */
    void afterInitialized(ApplicationContext context) throws Exception;

}
