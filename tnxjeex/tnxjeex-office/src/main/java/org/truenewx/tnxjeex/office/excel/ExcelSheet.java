package org.truenewx.tnxjeex.office.excel;

import java.util.Locale;
import java.util.function.BiConsumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.truenewx.tnxjee.core.caption.CaptionUtil;
import org.truenewx.tnxjee.model.spec.enums.support.EnumValueHelper;
import org.truenewx.tnxjeex.office.excel.exports.ExcelExportUtil;

/**
 * Excel工作表
 *
 * @author jianglei
 */
public class ExcelSheet {

    private Sheet origin;
    private ExcelDoc doc;

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

    public void replaceEnumValueToCaption(int columnIndex, Class<? extends Enum<?>> enumClass) {
        Locale locale = Locale.getDefault();
        forEach((row, rowIndex) -> {
            ExcelCell cell = row.getCell(columnIndex);
            if (cell != null) {
                String value = cell.getStringCellValue();
                Enum<?> enumConstant = EnumValueHelper.valueOf(enumClass, value);
                if (enumConstant != null) {
                    String caption = CaptionUtil.getCaption(enumConstant, locale);
                    if (!value.equals(caption)) {
                        cell.setCellValue(caption);
                    }
                }
            }
        }, 0);
    }

}
