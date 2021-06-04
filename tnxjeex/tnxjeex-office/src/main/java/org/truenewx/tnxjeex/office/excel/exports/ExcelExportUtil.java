package org.truenewx.tnxjeex.office.excel.exports;

import java.util.Iterator;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Excel文档工具类
 *
 * @author jianglei
 */
public class ExcelExportUtil {

    public static void cloneRows(Sheet sheet, int sourceStartRowIndex, int rowsNum, int targetStartRowIndex) {
        for (int i = 0; i < rowsNum; i++) {
            int sourceRowIndex = sourceStartRowIndex + i;
            Row sourceRow = sheet.getRow(sourceRowIndex);
            if (sourceRow != null) {
                int targetRowIndex = targetStartRowIndex + i;
                Row targetRow = sheet.getRow(targetRowIndex);
                if (targetRow == null) {
                    targetRow = sheet.createRow(targetRowIndex);
                }
                cloneRow(sourceRow, targetRow);
            }
        }
    }

    public static void cloneRow(Row source, Row target) {
        target.setHeight(source.getHeight());
        Sheet sheet = source.getSheet();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress address = sheet.getMergedRegion(i);
            if (address.getFirstRow() == source.getRowNum()) {
                CellRangeAddress newAddress = new CellRangeAddress(target.getRowNum(),
                        target.getRowNum() + address.getLastRow() - address.getFirstRow(), address.getFirstColumn(),
                        address.getLastColumn());
                sheet.addMergedRegion(newAddress);
            }
        }
        for (Iterator<Cell> iterator = source.cellIterator(); iterator.hasNext(); ) {
            Cell sourceCell = iterator.next();
            Cell newCell = target.createCell(sourceCell.getColumnIndex());
            cloneCell(sourceCell, newCell);
        }
    }

    public static void cloneCell(Cell source, Cell target) {
        // 一个文档中的个性化样式数量有4000的限制，克隆单元格时直接引用样式，而不是深度克隆一个全新的样式
        target.setCellStyle(source.getCellStyle());
        if (source.getCellComment() != null) {
            target.setCellComment(source.getCellComment());
        }
        // 不同数据类型处理
        switch (source.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(source)) {
                    target.setCellValue(source.getDateCellValue());
                } else {
                    target.setCellValue(source.getNumericCellValue());
                }
                break;
            case STRING:
                target.setCellValue(source.getRichStringCellValue());
                break;
            case FORMULA:
                target.setCellFormula(source.getCellFormula());
                break;
            case BLANK:
                target.setBlank();
                break;
            case BOOLEAN:
                target.setCellValue(source.getBooleanCellValue());
                break;
            case ERROR:
                target.setCellErrorValue(source.getErrorCellValue());
                break;
            default:
                break;
        }
    }

    public static void cloneFont(Font source, Font target) {
        target.setBold(source.getBold());
        target.setCharSet(source.getCharSet());
        target.setColor(source.getColor());
        target.setFontHeight(source.getFontHeight());
        target.setFontName(source.getFontName());
        target.setItalic(source.getItalic());
        target.setStrikeout(source.getStrikeout());
        target.setTypeOffset(source.getTypeOffset());
        target.setUnderline(source.getUnderline());
    }

    public static void mergeCells(Sheet sheet, int firstRowIndex, int firstColumnIndex, int lastRowIndex,
            int lastColumnIndex) {
        sheet.addMergedRegion(new CellRangeAddress(firstRowIndex, lastRowIndex, firstColumnIndex, lastColumnIndex));
    }

    public static void setBackgroundColor(CellStyle style, HSSFColorPredefined color) {
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public static void setCellHyperLink(Cell cell, String caption, String url) {
        cell.setCellFormula("HYPERLINK(\"" + url + "\",\"" + caption + "\")");
    }

}
