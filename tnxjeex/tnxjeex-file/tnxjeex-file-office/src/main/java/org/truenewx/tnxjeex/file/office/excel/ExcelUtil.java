package org.truenewx.tnxjeex.file.office.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjeex.file.office.excel.display.ExcelDisplayUtil;

public class ExcelUtil {

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

    public static String getConfiguredDatePattern(short dataFormat) {
        String key = String.valueOf(dataFormat);
        return DATE_PATTERN_PROPERTIES.getProperty(key);
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

}
