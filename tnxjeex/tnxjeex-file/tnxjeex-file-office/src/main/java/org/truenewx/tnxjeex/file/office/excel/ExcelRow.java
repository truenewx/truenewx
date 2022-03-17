package org.truenewx.tnxjeex.file.office.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Supplier;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjeex.file.office.excel.display.DisplayingExcelCellModel;
import org.truenewx.tnxjeex.file.office.excel.display.DisplayingExcelRowModel;

/**
 * Excel行
 *
 * @author jianglei
 */
public class ExcelRow {

    private static float HEIGHT_RATE = 96f / 72f;

    private Row origin;
    private ExcelSheet sheet;

    public ExcelRow(ExcelSheet sheet, Row origin) {
        this.origin = origin;
        this.sheet = sheet;
    }

    public Row getOrigin() {
        return this.origin;
    }

    public ExcelSheet getSheet() {
        return this.sheet;
    }

    public ExcelCell createCell(int columnIndex) {
        Cell cell = this.origin.createCell(columnIndex);
        return new ExcelCell(this, cell);
    }

    public ExcelCell getCell(int columnIndex, boolean createIfNull) {
        Cell cell = this.origin.getCell(columnIndex);
        if (cell == null && createIfNull) {
            return createCell(columnIndex);
        }
        return cell == null ? null : new ExcelCell(this, cell);
    }

    public ExcelCell getCell(int columnIndex) {
        return getCell(columnIndex, false);
    }

    public int getRowIndex() {
        return this.origin.getRowNum();
    }

    public ExcelRowAddress getAddress() {
        return new ExcelRowAddress(this.sheet.getOrigin().getSheetName(), this.sheet.getSheetIndex(), getRowIndex());
    }

    public ExcelCell setCellValue(int columnIndex, Object value) {
        ExcelCell cell = getCell(columnIndex, true);
        cell.setCellValue(value);
        return cell;
    }

    public void setHeightInPoints(Number height) {
        this.origin.setHeightInPoints(height.floatValue());
    }

    public String getStringCellValue(int columnIndex) {
        Cell cell = this.origin.getCell(columnIndex);
        return cell == null ? null : new ExcelCell(this, cell).getStringCellValue();
    }

    public BigDecimal getNumericCellValue(int columnIndex) {
        Cell cell = this.origin.getCell(columnIndex);
        return cell == null ? null : new ExcelCell(this, cell).getNumericCellValue();
    }

    public LocalDate getLocalDateCellValue(int columnIndex) {
        Cell cell = this.origin.getCell(columnIndex);
        return cell == null ? null : new ExcelCell(this, cell).getLocalDateCellValue();
    }

    public LocalDate getLocalMonthCellValue(int columnIndex) {
        Cell cell = this.origin.getCell(columnIndex);
        return cell == null ? null : new ExcelCell(this, cell).getLocalMonthCellValue();
    }

    public PermanentableDate getPermanentableDateCellValue(int columnIndex,
            Supplier<String> permanentDateTextSupplier) {
        Cell cell = this.origin.getCell(columnIndex);
        return cell == null ? null : new ExcelCell(this, cell).getPermanentableDateCellValue(permanentDateTextSupplier);
    }

    public DisplayingExcelRowModel toDisplayModel(int cellNum) {
        int rowIndex = getRowIndex();
        int number = rowIndex + 1;
        int height = (int) (this.origin.getHeightInPoints() * HEIGHT_RATE); // 高度算法并不精确，暂时无需精确结果
        boolean hidden = this.origin.getZeroHeight();

        DisplayingExcelRowModel rowModel = new DisplayingExcelRowModel(number, height, hidden);
        int beginCellIndex = this.origin.getFirstCellNum();
        if (beginCellIndex >= 0 && cellNum > 0) {
            ExcelSheet sheet = getSheet();
            DisplayingExcelCellModel[] cellModels = new DisplayingExcelCellModel[cellNum];
            for (int i = beginCellIndex; i < cellNum; i++) {
                ExcelCell cell = getCell(i);
                if (cell != null) {
                    Integer rowspan = null;
                    Integer colspan = null;
                    // 合并单元格处理
                    CellRangeAddress rangeAddress = sheet.locateMergedRegion(cell.getOrigin());
                    if (rangeAddress != null) {
                        if (cell.isFirstIn(rangeAddress)) { // 已合并且为合并区域的首个单元格，则计算占用行数和列数
                            rowspan = rangeAddress.getLastRow() - rangeAddress.getFirstRow() + 1;
                            colspan = rangeAddress.getLastColumn() - rangeAddress.getFirstColumn() + 1;
                        } else { // 已合并但不是合并区域的首个单元格，不占用行和列，不赋值
                            cell = null;
                        }
                    }
                    if (cell != null) {
                        String value = cell.getStringCellValue();
                        CellStyle style = cell.getCellStyle();
                        cellModels[i] = new DisplayingExcelCellModel(value, style, rowspan, colspan);
                    }
                } else { // 占位
                    cellModels[i] = new DisplayingExcelCellModel();
                }
            }
            rowModel.setCells(cellModels);
        }
        return rowModel;
    }

}
