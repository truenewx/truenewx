package org.truenewx.tnxjeex.doc.msoffice.excel.display;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

/**
 * Excel展示工具类
 */
public class ExcelDisplayUtil {

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

}
