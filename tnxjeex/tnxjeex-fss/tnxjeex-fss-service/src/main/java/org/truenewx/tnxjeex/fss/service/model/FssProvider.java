package org.truenewx.tnxjeex.fss.service.model;

import org.truenewx.tnxjee.core.caption.Caption;

/**
 * 文件存储服务提供商
 *
 * @author jianglei
 *
 */
public enum FssProvider {

    @Caption("阿里云")
    ALIYUN,

    @Caption("亚马逊云")
    AWS,

    @Caption("自有")
    OWN;

}
