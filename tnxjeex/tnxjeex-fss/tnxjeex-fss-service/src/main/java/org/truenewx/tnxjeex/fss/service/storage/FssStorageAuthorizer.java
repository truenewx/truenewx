package org.truenewx.tnxjeex.fss.service.storage;

import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 文件存储服务的底层存储授权器
 *
 * @author jianglei
 */
public interface FssStorageAuthorizer {

    /**
     * 获取当前授权器的服务提供商
     *
     * @return 服务提供商
     */
    FssStorageProvider getProvider();

    /**
     * 授权指定文件为公开可读
     *
     * @param storagePath 文件存储路径
     */
    void authorizePublicRead(String storagePath);

    /**
     * @return 读取地址的通用上下文地址，包含协议、域名（或IP）、可能的端口
     */
    String getReadContextUrl();

    /**
     * 获取指定文件读取地址
     *
     * @param userIdentity 用户标识
     * @param storagePath  文件存储路径
     * @return 文件读取地址
     */
    String getReadUrl(UserIdentity<?> userIdentity, String storagePath);

}
