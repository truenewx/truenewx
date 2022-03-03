package org.truenewx.tnxjee.service.impl.transaction;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.CompositeTransactionAttributeSource;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.truenewx.tnxjee.core.beans.factory.SourceBeanFactory;
import org.truenewx.tnxjee.service.transaction.annotation.Transactionable;

/**
 * 事务性自动代理创建器
 *
 * @author jianglei
 */
public class TransactionalAutoProxyCreator
        implements SmartInstantiationAwareBeanPostProcessor, BeanClassLoaderAware, Ordered {

    /**
     * 只读事务属性缩写
     */
    public static String READ_TRANSACTION_ATTRIBUTE_ABBR = "read";
    /**
     * 可写事务属性缩写
     */
    public static String WRITE_TRANSACTION_ATTRIBUTE_ABBR = "write";

    protected ClassLoader beanClassLoader;
    private SourceBeanFactory beanFactory;
    private PlatformTransactionManager transactionManager;

    private String readTransactionAttribute = "PROPAGATION_REQUIRED, readOnly";
    private String writeTransactionAttribute = "PROPAGATION_REQUIRED, ISOLATION_READ_COMMITTED, -Throwable";
    protected Properties transactionAttributes;

    private Map<String, Object> proxies = new ConcurrentHashMap<>(16);

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Autowired
    public void setBeanFactory(SourceBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Autowired(required = false) // 如果应用具有Service层但没有Repo层，则没有该实例
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setReadTransactionAttribute(String readTransactionAttribute) {
        this.readTransactionAttribute = readTransactionAttribute;
    }

    public void setWriteTransactionAttribute(String writeTransactionAttribute) {
        this.writeTransactionAttribute = writeTransactionAttribute;
    }

    public void setTransactionAttributes(Properties globalTransactionAttributes) {
        this.transactionAttributes = globalTransactionAttributes;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        Object proxy = getCachedProxy(beanName);
        return proxy == null ? null : proxy.getClass();
    }

    protected Object getCachedProxy(String beanName) {
        return this.proxies.get(beanName);
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return wrapIfNecessary(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return wrapIfNecessary(bean, beanName);
    }

    protected Object wrapIfNecessary(Object bean, String beanName) {
        if (this.transactionManager != null) {
            if (isProxiable(bean, beanName)) {
                return createProxy(bean, beanName);
            }
            if (AopUtils.isAopProxy(bean)) {
                this.proxies.put(beanName, bean);
            }
        }
        return bean;
    }

    protected boolean isProxiable(Object bean, String beanName) {
        if (!AopUtils.isAopProxy(bean) && !this.proxies.containsKey(beanName)) {
            Class<?>[] proxyInterfaces = getProxyInterfaces(bean.getClass());
            return proxyInterfaces != null && proxyInterfaces.length > 0;
        }
        return false;
    }

    protected Class<?>[] getProxyInterfaces(Class<?> beanClass) {
        Transactionable transactionable = beanClass.getAnnotation(Transactionable.class);
        return transactionable == null ? null : transactionable.proxyInterface();
    }

    protected Object createProxy(Object bean, String beanName) {
        if (this.transactionManager != null) {
            TransactionProxyFactoryBean factoryBean = new TransactionProxyFactoryBean();
            factoryBean.setBeanClassLoader(this.beanClassLoader);
            factoryBean.setBeanFactory(this.beanFactory);
            factoryBean.setTransactionManager(this.transactionManager);
            factoryBean.setTarget(bean);
            Class<?> beanClass = bean.getClass();
            factoryBean.setProxyInterfaces(getProxyInterfaces(beanClass));
            factoryBean.setTransactionAttributeSource(getTransactionAttributeSource(beanClass));
            factoryBean.afterPropertiesSet();
            Object proxy = factoryBean.getObject();
            this.proxies.put(beanName, proxy); // 缓存代理
            return proxy;
        }
        return null;
    }

    private TransactionAttributeSource getTransactionAttributeSource(Class<?> beanClass) {
        Properties attributes = getClassTransactionAttributes(beanClass);
        if (attributes.isEmpty()) { // 没有类级别事务属性配置，则只使用方法级别的注解配置
            return new AnnotationTransactionAttributeSource();
        } else { // 否则，需同时使用方法级别的注解配置和类级别的事务属性配置
            NameMatchTransactionAttributeSource nmtas = new NameMatchTransactionAttributeSource();
            nmtas.setProperties(attributes);
            return new CompositeTransactionAttributeSource(
                    new TransactionAttributeSource[]{ new AnnotationTransactionAttributeSource(), nmtas }); // 方法级别优先
        }
    }

    /**
     * 获取类级别的事务属性配置
     *
     * @param beanClass 类型
     * @return 类级别的事务属性配置
     */
    private Properties getClassTransactionAttributes(Class<?> beanClass) {
        Properties attributes = new Properties();
        if (this.transactionAttributes != null) { // 先加入全局默认事务配置
            attributes.putAll(this.transactionAttributes);
        }
        Transactionable transactionable = beanClass.getAnnotation(Transactionable.class);
        if (transactionable != null) {
            for (String methodNamePattern : transactionable.read()) {
                attributes.setProperty(methodNamePattern, this.readTransactionAttribute);
            }
            for (String methodNamePattern : transactionable.write()) {
                attributes.setProperty(methodNamePattern, this.writeTransactionAttribute);
            }
        }
        // 转换缩写
        for (Entry<Object, Object> entry : attributes.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String attribute = (String) entry.getValue();
                if (READ_TRANSACTION_ATTRIBUTE_ABBR.equalsIgnoreCase(attribute)) {
                    entry.setValue(this.readTransactionAttribute);
                } else if (WRITE_TRANSACTION_ATTRIBUTE_ABBR.equalsIgnoreCase(attribute)) {
                    entry.setValue(this.writeTransactionAttribute);
                }
            }
        }
        return attributes;
    }

}
