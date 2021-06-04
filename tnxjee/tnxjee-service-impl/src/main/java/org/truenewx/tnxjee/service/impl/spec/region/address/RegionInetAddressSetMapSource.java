package org.truenewx.tnxjee.service.impl.spec.region.address;

import java.util.Map;

import org.truenewx.tnxjee.core.spec.InetAddressSet;

/**
 * 区划-网络地址集合的映射集来源
 *
 * @author jianglei
 * @version 1.0.0 2014年7月14日
 * 
 */
public interface RegionInetAddressSetMapSource {
    /**
     * 获取区划-网络地址集合的映射集
     *
     * @return 区划-网络地址集合的映射集
     */
    Map<String, InetAddressSet> getMap();
}
