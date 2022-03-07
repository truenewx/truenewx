package org.truenewx.tnxjeex.file.office.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Supplier;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.truenewx.tnxjee.core.spec.PermanentableDate;

/**
 * Excel行
 *
 * @author jianglei
 */
public class ExcelRow {

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

}
