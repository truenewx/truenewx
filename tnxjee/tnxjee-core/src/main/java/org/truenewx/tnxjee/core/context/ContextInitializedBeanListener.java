package org.truenewx.tnxjee.core.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.beans.ContextInitializedBeanProxy;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 容器初始化完成后执行bean的监听器，找出所有容器初始化完成后执行bean并在容器初始化完成后执行。<br/>
 * 如果一个bean具有代理，则只执行代理
 *
 * @author jianglei
 */
@Component
public class ContextInitializedBeanListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, ContextInitializedBean> beans = context
                .getBeansOfType(ContextInitializedBean.class);
        Map<ContextInitializedBean, ContextInitializedBean> map = new HashMap<>();
        for (ContextInitializedBean bean : beans.values()) {
            if (bean instanceof ContextInitializedBeanProxy) {
                ContextInitializedBeanProxy proxy = (ContextInitializedBeanProxy) bean;
                ContextInitializedBean target = proxy.getTarget();
                if (target != null) {
                    map.remove(target);
                    map.put(target, proxy);
                }
            } else {
                map.putIfAbsent(bean, bean);
            }
        }
        for (ContextInitializedBean bean : map.values()) {
            try {
                bean.afterInitialized(context);
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            }
        }
    }
}
