package org.truenewx.tnxjee.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.beans.factory.SourceBeanFactory;
import org.truenewx.tnxjee.service.Service;
import org.truenewx.tnxjee.service.ServiceFactory;

/**
 * 服务工厂实现
 *
 * @author jianglei
 */
@org.springframework.stereotype.Service
public class ServiceFactoryImpl implements ServiceFactory, ServiceRegistrar {

    @Autowired
    private SourceBeanFactory beanFactory;

    private Map<Class<?>, Service> transactionalServices = new ConcurrentHashMap<>();
    private Map<Class<?>, Service> untransactionalServices = new ConcurrentHashMap<>();

    @Override
    public void register(Class<? extends Service> serviceInterface, Service transactionalService,
            Service untransactionalService) {
        if (transactionalService != null && serviceInterface.isInstance(transactionalService)) {
            this.transactionalServices.put(serviceInterface, transactionalService);
        }
        if (untransactionalService != null && serviceInterface.isInstance(untransactionalService)) {
            this.untransactionalServices.put(serviceInterface, untransactionalService);
        }
    }

    @Override
    public <S extends Service> S getService(Class<S> serviceClass) {
        return getService(serviceClass, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends Service> S getService(Class<S> serviceClass, boolean transactional) {
        if (transactional) {
            S service = (S) this.transactionalServices.get(serviceClass);
            if (service == null) { // 如果没有缓存，则尝试从bean工厂中获取并缓存
                try {
                    service = this.beanFactory.getBean(serviceClass);
                    this.transactionalServices.put(serviceClass, service);
                } catch (NoSuchBeanDefinitionException e) {
                    service = null;
                }
            }
            return service;
        } else {
            S service = (S) this.untransactionalServices.get(serviceClass);
            if (service == null) { // 如果没有缓存，则尝试从bean工厂中获取并缓存
                service = this.beanFactory.getSourceBean(serviceClass);
                if (service != null) {
                    this.untransactionalServices.put(serviceClass, service);
                }
            }
            return service;
        }
    }

}
