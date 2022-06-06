package org.truenewx.tnxjee.service;

/**
 * 服务工厂
 *
 * @author jianglei
 */
public interface ServiceFactory {

    /**
     * 获取指定类型的服务对象
     *
     * @param serviceClass  服务类型
     * @param transactional 是否获取带有事务的实现
     * @param <S>           服务类型
     * @return 服务对象
     */
    <S extends Service> S getService(Class<S> serviceClass, boolean transactional);

    /**
     * 获取指定类型的服务对象，默认获取带有事务的实现
     *
     * @param serviceClass 服务类型
     * @param <S>          服务类型
     * @return 服务对象
     */
    default <S extends Service> S getService(Class<S> serviceClass) {
        return getService(serviceClass, true);
    }

}
