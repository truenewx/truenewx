package org.truenewx.tnxjeex.file.office.excel.display;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjeex.file.office.excel.ExcelDoc;
import org.truenewx.tnxjeex.file.office.excel.ExcelSheet;

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

    public static void main(String[] args) throws IOException {
        String extension = FileExtensions.XLSX;
        String filename = "E:\\工作文档\\效行\\doc\\人事\\工作记录." + extension;
        filename = "C:\\Users\\jiang\\Desktop\\coding\\1." + extension;
        ExcelDoc doc = new ExcelDoc(new FileInputStream(filename), extension);
        ExcelSheet sheet = doc.getSheetAt(0);
        List<DisplayingExcelRowModel> rows = sheet.getDisplayRows();
//        for (DisplayingExcelRowModel row : rows) {
//            System.out.println(row.getNumber() + ": " + StringUtils.join(row.getData(), Strings.COMMA));
//        }
    }

}
