package org.truenewx.tnxjee.service.impl.spec.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.spec.region.*;

/**
 * @author jianglei
 */
public abstract class AbstractNationalRegionSource implements NationalRegionSource, MessageSourceAware {

    private static final String CITY_SUFFIXES_FORMAT = "constant.tnxjee.service.region.%s.city_suffixes";
    private static final String COUNTY_SUFFIXES_FORMAT = "constant.tnxjee.service.region.%s.county_suffixes";
    private static final String PROVINCE_LEVEL_CITY_SUFFIXES_FORMAT = "constant.tnxjee.service.region.%s.province_level_city_suffixes";
    private static final String CITY_LEVEL_COUNTY_SUFFIXES_FORMAT = "constant.tnxjee.service.region.%s.city_level_county_suffixes";

    /**
     * 国家代号，默认为中国
     */
    private String nationCode = RegionNationCodes.CHINA;
    /**
     * 显示区域-当前国家级行政区划的映射集
     */
    protected Map<Locale, Region> localeNationalRegionMap = new HashMap<>();
    /**
     * 显示区域-区划代号-行政区划的映射集
     */
    protected Map<Locale, Map<String, Region>> localeCodeSubsMap = new HashMap<>();
    /**
     * 显示区域-区划简称-行政区划的映射集
     */
    protected Map<Locale, Map<String, Region>> localeShortCaptionSubsMap = new HashMap<>();
    /**
     * 显示区域-区划全称-行政区划的映射集
     */
    protected Map<Locale, Map<String, Region>> localeFullCaptionSubsMap = new HashMap<>();

    private String[] citySuffixes = {};
    private String[] countySuffixes = {};
    private String[] provinceLevelCitySuffixes = {};
    private String[] cityLevelCountySuffixes = {};

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.citySuffixes = getTexts(messageSource, CITY_SUFFIXES_FORMAT);
        this.countySuffixes = getTexts(messageSource, COUNTY_SUFFIXES_FORMAT);
        this.provinceLevelCitySuffixes = getTexts(messageSource, PROVINCE_LEVEL_CITY_SUFFIXES_FORMAT);
        this.cityLevelCountySuffixes = getTexts(messageSource, CITY_LEVEL_COUNTY_SUFFIXES_FORMAT);
    }

    private String[] getTexts(MessageSource messageSource, String format) {
        String code = String.format(format, getNationCode().toLowerCase());
        String text = messageSource.getMessage(code, null, Locale.getDefault());
        return text.split(Strings.COMMA);
    }

    public void setNationCode(String nationCode) {
        Assert.isTrue(nationCode.length() == RegionSource.NATION_LENGTH,
                "The length of nation must be " + RegionSource.NATION_LENGTH);
        this.nationCode = nationCode.toUpperCase();
    }

    @Override
    public String getNationCode() {
        return this.nationCode;
    }

    @Override
    public Region getNationalRegion(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Region region = this.localeNationalRegionMap.get(locale);
        if (region == null) {
            region = buildNationalRegion(locale);
        }
        return region;
    }

    @Override
    public Region getSubRegion(String code, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Map<String, Region> codeSubsMap = this.localeCodeSubsMap.get(locale);
        if (codeSubsMap == null) {
            buildNationalRegion(locale);
            codeSubsMap = this.localeCodeSubsMap.get(locale);
        }
        if (codeSubsMap != null) {
            return codeSubsMap.get(code);
        }
        return null;
    }

    @Override
    public Region getSubRegion(String provinceCaption, String cityCaption, String countyCaption, boolean withSuffix,
            Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Map<String, Region> captionSubsMap = getCaptionSubsMap(withSuffix, locale);
        if (captionSubsMap != null) {
            StringBuilder caption = new StringBuilder(provinceCaption);
            if (cityCaption != null) {
                caption.append(cityCaption);
                if (countyCaption != null) { // 市级名称不为空，县级名称才有效
                    caption.append(countyCaption);
                }
            }
            return captionSubsMap.get(caption.toString());
        }
        return null;
    }

    private Map<String, Region> getCaptionSubsMap(boolean withSuffix, Locale locale) {
        Map<Locale, Map<String, Region>> localeCaptionSubsMap = withSuffix ? this.localeFullCaptionSubsMap : this.localeShortCaptionSubsMap;
        Map<String, Region> captionSubsMap = localeCaptionSubsMap.get(locale);
        if (captionSubsMap == null) {
            buildNationalRegion(locale);
            captionSubsMap = localeCaptionSubsMap.get(locale);
        }
        return captionSubsMap;
    }

    protected abstract Region buildNationalRegion(Locale locale);

    @Override
    public Region parseSubRegion(String caption, boolean withSuffix, Locale locale) {
        Map<String, Region> captionSubsMap = getCaptionSubsMap(withSuffix, locale);
        if (captionSubsMap != null) {
            return captionSubsMap.get(caption);
        }
        return null;
    }

    @Override
    public RegionAddress parseAddress(String address, int level, Locale locale) {
        int normalProvinceCaptionLength = 2; // 一般的省级区划名称长度为2
        if (address == null || address.length() < normalProvinceCaptionLength) { // 地址至少为一般省级区划名称长度
            return null;
        }
        Region nationalRegion = getNationalRegion(locale);
        if (nationalRegion.getLevel() >= level) {
            return new RegionAddress(nationalRegion, address);
        }

        // 先查找省级区划
        Region province = null;
        try {
            province = nationalRegion.findSubByCaption(address.substring(0, normalProvinceCaptionLength), false);
            if (province == null) { // 再尝试匹配长度为3的省级名称，中国暂不存在长度超过3的省级名称，国外的情况暂时不考虑
                province = nationalRegion
                        .findSubByCaption(address.substring(0, normalProvinceCaptionLength + 1), false);
            }
        } catch (Exception ignored) {
        }
        if (province == null) { // 无法正确匹配省级区划
            throw new BusinessException(RegionExceptionCodes.PROVINCE_ERROR);
        }

        // 此时省级区划一定已经找到，去掉省级区划名称后，进一步查找市级区划
        String provinceCaption = province.getCaption(true);
        if (!address.startsWith(provinceCaption)) { // 不以带后缀的名称开头，则取不带后缀的名称
            provinceCaption = province.getCaption(false);
        }
        String regionCaption = address.substring(provinceCaption.length());
        if (province.getLevel() >= level) {
            return new RegionAddress(province, regionCaption);
        }

        Region city = null;
        // 如果省级区划为直辖市，则市级区划后缀需是特殊的市级区县后缀
        boolean provincialCity = ArrayUtils.contains(this.provinceLevelCitySuffixes, province.getSuffix());
        String[] citySuffixes = provincialCity ? this.cityLevelCountySuffixes : this.citySuffixes;
        int index = StringUtil.indexOfFirstInTurn(regionCaption, citySuffixes, true);
        if (index > 0) {
            String cityCaption = regionCaption.substring(0, index);
            city = province.findSubByCaption(cityCaption, false);
        }
        if (city == null) { // 无法正确匹配市级区划
            throw new BusinessException(RegionExceptionCodes.CITY_ERROR);
        }

        // 此时市级区划一定已经找到，去掉市级区划名称后，查找县级区划
        Region county = null;
        // 直辖市的下级市级行政区划就是县级区划，无需再往下找县级区划
        if (provincialCity) {
            county = city;
        } else {
            String cityCaption = city.getCaption(true);
            if (!regionCaption.startsWith(cityCaption)) { // 不以带后缀的名称开头，则取不带后缀的名称
                cityCaption = city.getCaption(false);
            }
            regionCaption = regionCaption.substring(cityCaption.length());
            if (city.getLevel() >= level) {
                return new RegionAddress(city, regionCaption);
            }
            index = StringUtil.indexOfFirstInTurn(regionCaption, this.countySuffixes, true);
            if (index > 0) {
                String countyCaption = regionCaption.substring(0, index);
                county = city.findSubByCaption(countyCaption, false);
            }
            if (county == null) { // 无法正确匹配县级区划
                throw new BusinessException(RegionExceptionCodes.COUNTY_ERROR);
            }
        }

        // 此时县级区划一定已经找到，去掉县级区划名称后，剩下的是地址详情
        String countyCaption = county.getCaption(true);
        if (!regionCaption.startsWith(countyCaption)) { // 不以带后缀的名称开头，则取不带后缀的名称
            countyCaption = county.getCaption(false);
        }
        String detail = regionCaption.substring(countyCaption.length());
        return new RegionAddress(county, detail);
    }

}
