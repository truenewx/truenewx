package org.truenewx.tnxjeex.doc.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjeex.doc.excel.display.ExcelDisplayUtil;

public class ExcelUtil {

    public static final short[] RGB_WHITE = { 0xff, 0xff, 0xff };
    public static final short[] RGB_BLACK = { 0x00, 0x00, 0x00 };

    private ExcelUtil() {
    }

    /**
     * 日期格式属性集
     */
    private static final Properties DATE_PATTERN_PROPERTIES = new Properties();

    static {
        Resource resource = IOUtil.findI18nResource("classpath:META-INF/date-patterns", Locale.getDefault(),
                FileExtensions.PROPERTIES);
        if (resource != null) {
            try {
                InputStream in = resource.getInputStream();
                DATE_PATTERN_PROPERTIES.load(in);
                in.close();
            } catch (IOException e) {
                LogUtil.error(ExcelDisplayUtil.class, e);
            }
        }
    }

    public static String getConfiguredDataPattern(String dataFormat) {
        return DATE_PATTERN_PROPERTIES.getProperty(dataFormat);
    }

    public static HorizontalAlignment getDefaultAlignment(CellType cellType) {
        switch (cellType) {
            case STRING:
                return HorizontalAlignment.LEFT;
            case BOOLEAN:
                return HorizontalAlignment.CENTER;
            default:
                return HorizontalAlignment.RIGHT;
        }
    }

    public static String getHex(Color color, short[] ignoredRgb) {
        if (color instanceof ExtendedColor) {
            ExtendedColor extendedColor = (ExtendedColor) color;
            byte[] rgb = extendedColor.getRGBWithTint();
            if (ignoredRgb == null || !ArrayUtil.deepEquals(rgb, ignoredRgb)) {
                StringBuilder hex = new StringBuilder(Strings.WELL);
                for (byte b : rgb) {
                    hex.append(toHexString(b));
                }
                return hex.toString().toUpperCase(Locale.ROOT);
            }
        } else if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor) color;
            short[] rgb = hssfColor.getTriplet();
            if (ignoredRgb == null || !Objects.deepEquals(rgb, ignoredRgb)) {
                StringBuilder hex = new StringBuilder(Strings.WELL);
                for (short s : rgb) {
                    hex.append(toHexString(s));
                }
                return hex.toString().toUpperCase(Locale.ROOT);
            }
        }
        return null;
    }

    private static String toHexString(short s) {
        String hex = Integer.toHexString(s & 0xff);
        return StringUtils.leftPad(hex, 2, "0");
    }

}
