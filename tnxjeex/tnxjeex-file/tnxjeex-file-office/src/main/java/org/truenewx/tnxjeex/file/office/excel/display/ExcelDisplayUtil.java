package org.truenewx.tnxjeex.file.office.excel.display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * Excel展示工具类
 */
public class ExcelDisplayUtil {

    /**
     * 日期格式属性集
     */
    private static final Properties DATE_PATTERN_PROPERTIES = new Properties();

    static {
        Resource resource = IOUtil.findI18nResource("classpath:META-INF/date-patterns", Locale.getDefault(),
                FileExtensions.PROPERTIES);
        if (resource != null) {
            try {
                DATE_PATTERN_PROPERTIES.load(resource.getInputStream());
            } catch (IOException e) {
                LogUtil.error(ExcelDisplayUtil.class, e);
            }
        }
    }

    private ExcelDisplayUtil() {
    }

    public static int getColumnNum(Sheet sheet) {
        int beginRowIndex = sheet.getFirstRowNum();
        int endRowIndex = sheet.getLastRowNum();
        int columnNum = 0;
        for (int i = beginRowIndex; i <= endRowIndex; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                columnNum = Math.max(columnNum, row.getLastCellNum());
            }
        }
        return columnNum;
    }

    public static List<DisplayingExcelColumnModel> getColumns(Sheet sheet) {
        List<DisplayingExcelColumnModel> columns = new ArrayList<>();
        int columnNum = getColumnNum(sheet);
        for (int i = 0; i < columnNum; i++) {
            String address = CellReference.convertNumToColString(i);
            int width = sheet.getColumnWidth(i) / 32; // 该算法并不精确，暂时无需精确结果
            boolean hidden = sheet.isColumnHidden(i);
            columns.add(new DisplayingExcelColumnModel(address, width, hidden));
        }
        return columns;
    }

    public static String getConfiguredDatePattern(short dataFormat) {
        String key = String.valueOf(dataFormat);
        return DATE_PATTERN_PROPERTIES.getProperty(key);
    }

}
