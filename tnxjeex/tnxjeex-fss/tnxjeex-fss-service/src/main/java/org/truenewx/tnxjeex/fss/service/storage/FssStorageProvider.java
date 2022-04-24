package org.truenewx.tnxjeex.fss.service.storage;

import org.truenewx.tnxjee.core.caption.Caption;

/**
 * 文件存储服务的底层存储提供商
 *
 * @author jianglei
 */
public enum FssStorageProvider {

    @Caption("阿里云")
    ALIYUN,

    @Caption("亚马逊云")
    AWS,

    @Caption("自有")
    OWN;

}
