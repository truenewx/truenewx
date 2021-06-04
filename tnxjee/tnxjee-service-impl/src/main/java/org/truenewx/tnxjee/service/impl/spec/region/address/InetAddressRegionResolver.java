package org.truenewx.tnxjee.service.impl.spec.region.address;

import java.net.InetAddress;

/**
 * 网络地址->区划解决器
 *
 * @author jianglei
 * 
 */
public interface InetAddressRegionResolver {
    /**
     * 获取指定网络地址对应的区划代号
     *
     * @param address 网络地址
     * @return 区划代号
     */
    String resolveRegionCode(InetAddress address);
}
