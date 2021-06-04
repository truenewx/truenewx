package org.truenewx.tnxjee.core.parser;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.DateUtil;

import freemarker.template.Configuration;

/**
 * FreeMarker助手
 *
 * @author jianglei
 * 
 */
public class FreeMarkerHelper {

    private FreeMarkerHelper() {
    }

    public static Configuration getDefaultConfiguration() {
        Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        applyDefault(config);
        return config;
    }

    public static void applyDefault(Configuration config) {
        config.setClassicCompatible(true);
        config.setNumberFormat("0.##");
        config.setTimeFormat(DateUtil.TIME_PATTERN);
        config.setDateFormat(DateUtil.SHORT_DATE_PATTERN);
        config.setDateTimeFormat(DateUtil.LONG_DATE_PATTERN);
        config.clearEncodingMap();
        config.setDefaultEncoding(Strings.ENCODING_UTF8);
    }

}
