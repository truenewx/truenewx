package org.truenewx.tnxjeex.fss.service;

import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

/**
 * 文件存储授权器
 *
 * @author jianglei
 */
public interface FssAuthorizer {

    /**
     * 获取当前授权器的服务提供商
     *
     * @return 服务提供商
     */
    FssProvider getProvider();

    /**
     * 授权指定资源为公开可读
     * @param path   资源路径
     */
    void authorizePublicRead(String path);

    /**
     * 获取指定资源读取URL
     *
     * @param userIdentity 用户标识
     * @param path         资源路径
     * @return 资源读取URL
     */
    String getReadUrl(UserIdentity<?> userIdentity, String path);

}
