package org.truenewx.tnxjee.service.spec.region;

import java.util.Collection;
import java.util.Locale;

/**
 * 行政区划来源
 *
 * @author jianglei
 */
public interface RegionSource {
    /**
     * 国家代号的长度
     */
    int NATION_LENGTH = 2;

    NationalRegionSource getNationalRegionSource(String nationCode);

    /**
     * 获取指定行政区划，其显示名为指定显示区域下的文本<br>
     *
     * @param regionCode 行政区划代号
     * @param locale     显示区域
     * @return 对应区划选项
     */

    Region getRegion(String regionCode, Locale locale);

    /**
     * 获取指定国家的指定行政区划的选项
     *
     * @param nationCode      国家代号
     * @param provinceCaption 省份名称
     * @param cityCaption     市名称
     * @param countyCaption   县名称
     * @param withSuffix      省市名称中是否包含后缀
     * @param locale          显示区域
     * @return 对应区划选项
     */

    Region getRegion(String nationCode, String provinceCaption, String cityCaption, String countyCaption,
            boolean withSuffix, Locale locale);

    /**
     * 获取国家级区划选项集，其选项显示名为指定显示区域下的文本
     *
     * @param locale 显示区域
     * @return 国家级区划选项集
     */
    Collection<Region> getNationalRegions(Locale locale);

    Region getNationalRegion(String nationCode, Locale locale);

}
