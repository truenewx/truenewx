package org.truenewx.tnxjee.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.service.Service;
import org.truenewx.tnxjee.service.ServiceFactory;

/**
 * 服务支持，具有快速获取其它服务的能力
 *
 * @author jianglei
 */
public abstract class ServiceSupport {

    @Autowired
    private ServiceFactory serviceFactory;

    protected final <S extends Service> S getService(Class<S> serviceClass) {
        S service = this.serviceFactory.getService(serviceClass, false); // 默认取非事务性服务
        if (service == null) { // 如果没有非事务性服务，则取事务性服务
            service = this.serviceFactory.getService(serviceClass, true);
        }
        return service;
    }

}
