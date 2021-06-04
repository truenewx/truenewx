package org.truenewx.tnxjee.service.impl.transaction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.service.Service;
import org.truenewx.tnxjee.service.impl.ServiceRegistrar;

/**
 * 服务自动代理创建器
 *
 * @author jianglei
 */
@Component
public class ServiceAutoProxyCreator extends TransactionalAutoProxyCreator
        implements InitializingBean {

    private ServiceRegistrar serviceRegistrar;

    @Autowired
    public void setServiceRegistrar(ServiceRegistrar serviceRegistrar) {
        this.serviceRegistrar = serviceRegistrar;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.transactionAttributes == null) { // 如果未配置事务属性则初始化默认配置事务属性
            this.transactionAttributes = new Properties();
            this.transactionAttributes.put("get*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("find*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("query*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("load*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("count*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("is*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("validate*", READ_TRANSACTION_ATTRIBUTE_ABBR);

            this.transactionAttributes.put("add*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("update*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("delete*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("save*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object wrapIfNecessary(Object bean, String beanName) {
        if (isProxiable(bean, beanName)) {
            // 为服务父类中的全局事务属性配置添加代理
            bean = BeanUtil.getTargetSource(bean); // 取到原始目标对象

            Object proxy = createProxy(bean, beanName);
            // 注册事务性bean和非事务性bean
            Class<?>[] proxyInterfaces = getProxyInterfaces(bean.getClass());
            for (Class<?> proxyInterface : proxyInterfaces) {
                this.serviceRegistrar.register((Class<? extends Service>) proxyInterface,
                        (Service) proxy, (Service) bean);
            }
            return proxy;
        }
        return super.wrapIfNecessary(bean, beanName);
    }

    @Override
    protected boolean isProxiable(Object bean, String beanName) {
        // 未被缓存的服务，可取得代理接口类型，即可代理
        if (bean instanceof Service && getCachedProxy(beanName) == null) {
            Class<?> beanClass = bean.getClass();
            if (AopUtils.isAopProxy(bean) && bean instanceof Advised) { // 代理需取目标类型
                Advised proxy = (Advised) bean;
                beanClass = proxy.getTargetClass();
                if (beanClass == null) {
                    return false;
                }
            }
            Class<?>[] proxyInterfaces = getProxyInterfaces(beanClass);
            return proxyInterfaces != null && proxyInterfaces.length > 0;
        }
        return super.isProxiable(bean, beanName);
    }

    @Override
    protected Class<?>[] getProxyInterfaces(Class<?> beanClass) {
        if (Service.class.isAssignableFrom(beanClass)) {
            // 默认取Service实现类所有实现的最底层接口
            Set<Class<?>> proxyInterfaces = new HashSet<>();
            Set<Class<?>> beanInterfaces = ClassUtils.getAllInterfacesForClassAsSet(beanClass,
                    this.beanClassLoader);
            for (Class<?> beanInterface : beanInterfaces) {
                mergeInterface(proxyInterfaces, beanInterface);
            }
            return proxyInterfaces.toArray(new Class<?>[proxyInterfaces.size()]);
        }
        return super.getProxyInterfaces(beanClass);
    }

    private void mergeInterface(Set<Class<?>> interfaces, Class<?> interfaceClass) {
        for (Iterator<Class<?>> iterator = interfaces.iterator(); iterator.hasNext(); ) {
            Class<?> next = iterator.next();
            if (interfaceClass.isAssignableFrom(next)) { // 如果是已有接口的父接口，则忽略直接结束
                return;
            }
            if (next.isAssignableFrom(interfaceClass)) { // 如果是已有接口的子接口，则移除该父接口
                iterator.remove();
            }
        }
        interfaces.add(interfaceClass); // 最后加入指定接口
    }
}
