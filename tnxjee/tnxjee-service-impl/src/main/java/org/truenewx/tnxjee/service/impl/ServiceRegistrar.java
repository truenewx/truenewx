package org.truenewx.tnxjee.service.impl;

import org.truenewx.tnxjee.service.Service;

/**
 * 服务注册器
 *
 * @author jianglei
 * 
 */
public interface ServiceRegistrar {
    /**
     * 注册事务性服务和非事务性服务
     *
     * @param serviceInterface       服务接口
     * @param transactionalService   事务性服务
     * @param untransactionalService 非事务性服务
     */
    void register(Class<? extends Service> serviceInterface, Service transactionalService,
            Service untransactionalService);
}
