package org.truenewx.tnxjeex.file.office.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.truenewx.tnxjeex.file.office.excel.display.DisplayingExcelColumnModel;
import org.truenewx.tnxjeex.file.office.excel.display.DisplayingExcelRowModel;
import org.truenewx.tnxjeex.file.office.excel.display.DisplayingExcelSheetSummary;
import org.truenewx.tnxjeex.file.office.excel.display.ExcelDisplayUtil;
import org.truenewx.tnxjeex.file.office.excel.exports.ExcelExportUtil;

/**
 * Excel工作表
 *
 * @author jianglei
 */
public class ExcelSheet {

    private Sheet origin;
    private ExcelDoc doc;
    private List<CellRangeAddress> mergedRegions;

    public ExcelSheet(ExcelDoc doc, Sheet origin) {
        this.doc = doc;
        this.origin = origin;
    }

    public Sheet getOrigin() {
        return this.origin;
    }

    public ExcelDoc getDoc() {
        return this.doc;
    }

    public int getSheetIndex() {
        return this.doc.getOrigin().getSheetIndex(this.origin);
    }

    public int getRowNum() {
        return this.origin.getLastRowNum() + 1;
    }

    public int getColumnNum() {
        return ExcelDisplayUtil.getColumnNum(this.origin);
    }

    public ExcelRow createRow(int rowIndex, boolean copyStyleFromPreviousRow) {
        Row row = this.origin.createRow(rowIndex);
        if (copyStyleFromPreviousRow) {
            Row prevRow = this.origin.getRow(rowIndex - 1);
            if (prevRow != null) {
                row.setRowStyle(prevRow.getRowStyle());
                int cellNum = prevRow.getLastCellNum();
                for (int i = 0; i < cellNum; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(prevRow.getCell(i).getCellStyle());
                }
            }
        }
        return new ExcelRow(this, row);
    }

    public ExcelRow createRow(int rowIndex) {
        return createRow(rowIndex, false);
    }

    public ExcelRow getRow(int rowIndex, boolean createIfNull) {
        Row row = this.origin.getRow(rowIndex);
        if (row == null && createIfNull) {
            return createRow(rowIndex);
        }
        return row == null ? null : new ExcelRow(this, row);
    }

    public ExcelRow getRow(int rowIndex) {
        return getRow(rowIndex, false);
    }

    public void mergeCells(int firstRowIndex, int firstColumnIndex, int lastRowIndex, int lastColumnIndex) {
        ExcelExportUtil.mergeCells(this.origin, firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex);
    }

    public void forEach(BiConsumer<ExcelRow, Integer> consumer, int startIndex) {
        for (int i = startIndex; i <= this.origin.getLastRowNum(); i++) {
            Row row = this.origin.getRow(i);
            if (row != null) {
                consumer.accept(new ExcelRow(this, row), i);
            }
        }
    }

    public List<CellRangeAddress> getMergedRegions() {
        if (this.mergedRegions == null) {
            this.mergedRegions = this.origin.getMergedRegions();
        }
        return this.mergedRegions;
    }

    /**
     * 找出指定单元格在当前工作表中的所属合并区域
     *
     * @param cell 单元格
     * @return 指定单元格在当前工作表中的所属合并区域，null-不属于任何合并区域
     */
    public CellRangeAddress locateMergedRegion(Cell cell) {
        List<CellRangeAddress> mergedRegions = getMergedRegions();
        for (CellRangeAddress rangeAddress : mergedRegions) {
            if (rangeAddress.isInRange(cell)) {
                return rangeAddress;
            }
        }
        return null;
    }

    public DisplayingExcelSheetSummary getDisplaySummary() {
        List<DisplayingExcelColumnModel> columns = ExcelDisplayUtil.getColumns(this.origin);
        return new DisplayingExcelSheetSummary(columns, getRowNum());
    }

    public List<DisplayingExcelRowModel> getDisplayRows(int batchSize, int batchNo) {
        List<DisplayingExcelRowModel> displayRows = new ArrayList<>();
        batchNo = Math.max(batchNo, 1); // 批次号最小为1
        // 批次大小大于0才进行分批获取
        int beginRowIndex = batchSize > 0 ? (batchSize * (batchNo - 1)) : 0;
        int endRowIndex = batchSize > 0 ? (beginRowIndex + batchSize - 1) : this.origin.getLastRowNum();
        int columNum = getColumnNum();
        for (int i = beginRowIndex; i <= endRowIndex; i++) {
            ExcelRow row = getRow(i, true);
            displayRows.add(row.toDisplayModel(columNum));
        }
        return displayRows;
    }

}
