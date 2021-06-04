package org.truenewx.tnxjee.service.spec.region;

import java.util.Locale;

/**
 * 具体国家的行政区划来源
 *
 * @author jianglei
 */
public interface NationalRegionSource {
    /**
     * 获取国家代号
     *
     * @return 国家代号
     */
    String getNationCode();

    /**
     * 获取当前国家的行政区划，其显示名为指定显示区域下的文本
     *
     * @param locale 显示区域
     * @return 当前国家的行政区划
     */
    Region getNationalRegion(Locale locale);

    /**
     * 获取指定行政区划代号对应的行政区划
     *
     * @param code   行政区划代号
     * @param locale 显示区域
     * @return 行政区划
     */

    Region getSubRegion(String code, Locale locale);

    /**
     * 获取指定行政区划名称对应的行政区划
     *
     * @param provinceCaption 省份名称
     * @param cityCaption     市名称
     * @param countyCaption   县名称
     * @param withSuffix      显示名称中的省市名称是否包含后缀
     * @param locale          显示区域
     * @return 行政区划
     */

    Region getSubRegion(String provinceCaption, String cityCaption, String countyCaption, boolean withSuffix,
            Locale locale);

    /**
     * 从行政区划名称中解析行政区划代号
     *
     * @param caption    行政区划名称，不能包含多余文字
     * @param withSuffix 省市名称是否包含后缀
     * @param locale     显示区域
     * @return 行政区划代号
     */
    Region parseSubRegion(String caption, boolean withSuffix, Locale locale);

    /**
     * 从包含行政区划名称的地址中解析出行政区划代号，剩余部分作为详细地址
     *
     * @param address 包含行政区划名称的地址
     * @param level   需要解析到的级别，超出的级别不解析
     * @param locale  显示区域
     * @return 包含行政区划代号的地址
     */
    RegionAddress parseAddress(String address, int level, Locale locale);

}
