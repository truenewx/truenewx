package org.truenewx.tnxjee.service.impl.spec.region.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.InetAddressRange;
import org.truenewx.tnxjee.core.spec.InetAddressSet;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionNationCodes;
import org.truenewx.tnxjee.service.spec.region.RegionSource;

/**
 * Cz88的区划-网络地址集合映射集解析器
 *
 * @author jianglei
 * @version 1.0.0 2014年7月14日
 */
public class Cz88RegionInetAddressSetMapParser implements RegionInetAddressSetMapParser {

    private RegionSource regionSource;
    private String defaultNation = RegionNationCodes.CHINA;

    private String[] removedProvinceSuffixes = new String[0];
    private String[] provinceSuffixes = new String[0];
    private String[] citySuffixes = new String[0];
    private String[] countySuffixes = new String[0];
    private Map<String, String> lineMapping = new HashMap<>();
    private Map<String, String> provinceMapping = new HashMap<>();
    private Map<String, String> cityMapping = new HashMap<>();
    private Map<String, String> countyMapping = new HashMap<>();

    public void setRegionSource(RegionSource regionSource) {
        this.regionSource = regionSource;
    }

    public void setDefaultNation(String defaultNation) {
        this.defaultNation = defaultNation;
    }

    public void setRemovedProvinceSuffixes(String[] removedProvinceSuffixes) {
        this.removedProvinceSuffixes = removedProvinceSuffixes;
    }

    public void setRemovedProvinceSuffix(String removedProvinceSuffix) {
        this.removedProvinceSuffixes = removedProvinceSuffix.split(Strings.COMMA);
    }

    public void setProvinceSuffixes(String[] provinceSuffixes) {
        this.provinceSuffixes = provinceSuffixes;
    }

    public void setProvinceSuffix(String provinceSuffix) {
        this.provinceSuffixes = provinceSuffix.split(Strings.COMMA);
    }

    public void setCitySuffixes(String[] citySuffixes) {
        this.citySuffixes = citySuffixes;
    }

    public void setCitySuffix(String citySuffix) {
        this.citySuffixes = citySuffix.split(Strings.COMMA);
    }

    public void setCountySuffixes(String[] countySuffixes) {
        this.countySuffixes = countySuffixes;
    }

    public void setCountySuffix(String countySuffix) {
        this.countySuffixes = countySuffix.split(Strings.COMMA);
    }

    public void setLineMapping(Map<String, String> lineMapping) {
        this.lineMapping = lineMapping;
    }

    public void setProvinceMapping(Map<String, String> provinceMapping) {
        this.provinceMapping = provinceMapping;
    }

    public void setCityMapping(Map<String, String> cityMapping) {
        this.cityMapping = cityMapping;
    }

    public void setCountyMapping(Map<String, String> countyMapping) {
        this.countyMapping = countyMapping;
    }

    @Override
    public Map<String, InetAddressSet> parse(InputStream in, Locale locale, String encoding)
            throws IOException {
        Map<String, InetAddressSet> result = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
        String line = reader.readLine();
        while (line != null) {
            line = transLine(line);
            try {
                parseLineTo(line, result, locale);
            } catch (Exception e) { // 仅打印异常堆栈，不影响后续行的解析
                LogUtil.debug(getClass(), e);
            }
            line = reader.readLine();
        }
        return result;
    }

    private String transLine(String line) {
        if (StringUtils.isNotBlank(line)) {
            for (Entry<String, String> entry : this.lineMapping.entrySet()) {
                String translatedLine = line.replaceAll(entry.getKey(), entry.getValue());
                if (!line.equals(translatedLine)) { // 一旦有一个转换，则立即返回
                    return translatedLine;
                }
            }
        }
        return line;
    }

    private void parseLineTo(String line, Map<String, InetAddressSet> map, Locale locale) {
        if (StringUtils.isNotBlank(line) && line.indexOf(Strings.DOT) > 0) { // 包括点号才包含有效的IPv4地址
            String[] array = line.split(" +");
            if (array.length >= 3) {
                Inet4Address begin = NetUtil.getInet4Address(array[0]);
                Inet4Address end = NetUtil.getInet4Address(array[1]);
                if (begin != null && end != null) { // 有效的IP地址才考虑后续
                    String regionCode = parseRegionCode(array[2], ArrayUtil.get(array, 3), locale);
                    if (regionCode != null) {
                        InetAddressSet set = map.get(regionCode);
                        if (set == null) {
                            set = new InetAddressSet();
                            map.put(regionCode, set);
                        }
                        set.add(new InetAddressRange<>(begin, end));
                    }
                }
            }
        }
    }

    private String parseRegionCode(String caption, String remark, Locale locale) {
        String provinceCaption = null;
        for (String suffix : this.provinceSuffixes) {
            int index = caption.indexOf(suffix);
            if (index >= 0) { // 匹配到省级名称后缀
                index += suffix.length(); // 定位到后缀后一位以便于截取
                provinceCaption = caption.substring(0, index);
                provinceCaption = transCaption(provinceCaption, this.provinceMapping);
                // 最后移除省份名称中需要移除的后缀
                for (String removedSuffix : this.removedProvinceSuffixes) {
                    if (provinceCaption.endsWith(removedSuffix)) {
                        provinceCaption = provinceCaption.substring(0,
                                provinceCaption.length() - removedSuffix.length());
                        break;
                    }
                }
                caption = caption.substring(index); // 去掉省级名称部分以便于后续查找
                break;
            }
        }
        if (provinceCaption != null) { // 找到省级名称才可能有效
            String cityCaption = null; // 市级可以为空
            for (String suffix : this.citySuffixes) {
                int index = caption.indexOf(suffix);
                if (index >= 0) {
                    index += suffix.length(); // 定位到后缀后一位以便于截取
                    cityCaption = caption.substring(0, index);
                    cityCaption = transCaption(cityCaption, this.cityMapping);
                    caption = caption.substring(index); // 去掉市级名称部分以便于后续查找
                    break;
                }
            }
            String countyCaption = null; // 县级可以为空
            if (cityCaption != null) { // 找到市级找县级才有意义
                for (String suffix : this.countySuffixes) {
                    int index = caption.indexOf(suffix);
                    if (index >= 0) {
                        index += suffix.length(); // 定位到后缀后一位以便于截取
                        countyCaption = caption.substring(0, index);
                        countyCaption = transCaption(countyCaption, this.countyMapping);
                        break;
                    }
                }
            }
            if (countyCaption == null && remark != null) { // 未找到县级，则尝试从备注中转换获取
                if (cityCaption == null) { // 此时如果市级为空，则从备注中转换获取的为市级
                    for (String suffix : this.citySuffixes) {
                        int index = remark.indexOf(suffix);
                        if (index >= 0) {
                            index += suffix.length(); // 定位到后缀后一位以便于截取
                            cityCaption = remark.substring(0, index);
                            cityCaption = transCaption(cityCaption, this.cityMapping);
                            break;
                        }
                    }
                } else { // 否则转换获取的为县级
                    for (String suffix : this.countySuffixes) {
                        int index = remark.indexOf(suffix);
                        if (index >= 0) {
                            index += suffix.length(); // 定位到后缀后一位以便于截取
                            countyCaption = remark.substring(0, index);
                            countyCaption = transCaption(countyCaption, this.countyMapping);
                        }
                    }
                }
            }
            Region region = this.regionSource.getRegion(this.defaultNation, provinceCaption,
                    cityCaption, countyCaption, true, locale);
            // 如果无法取得区划选项，则尝试取上一级的区划选项
            if (region == null && countyCaption != null) {
                region = this.regionSource.getRegion(this.defaultNation, provinceCaption,
                        cityCaption, null, true, locale);
            }
            if (region == null && cityCaption != null) {
                region = this.regionSource.getRegion(this.defaultNation, provinceCaption, null,
                        null, true, locale);
            }
            if (region != null) {
                return region.getCode();
            }
        }
        return null;
    }

    private String transCaption(String caption, Map<String, String> mapping) {
        String value = mapping.get(caption);
        return value == null ? caption : value;
    }
}
