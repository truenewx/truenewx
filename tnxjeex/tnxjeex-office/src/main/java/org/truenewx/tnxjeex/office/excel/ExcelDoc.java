package org.truenewx.tnxjeex.office.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.truenewx.tnxjeex.office.excel.exports.ExcelExportUtil;

/**
 * Excel文档
 *
 * @author jianglei
 */
public class ExcelDoc {

    public static final String EXTENSION_XLS = "xls";
    public static final String EXTENSION_XLSX = "xlsx";

    private Workbook origin;
    private FormulaEvaluator evaluator;
    private Map<String, CellStyle> styles = new HashMap<>();
    private Map<String, Font> fonts = new HashMap<>();

    public ExcelDoc(String extension) {
        if (EXTENSION_XLS.equalsIgnoreCase(extension)) {
            this.origin = new HSSFWorkbook();
        } else {
            this.origin = new XSSFWorkbook();
        }
    }

    public ExcelDoc(InputStream in, String extension) throws IOException {
        if (EXTENSION_XLS.equalsIgnoreCase(extension)) {
            this.origin = new HSSFWorkbook(in);
        } else {
            this.origin = new XSSFWorkbook(in);
        }
    }

    public Workbook getOrigin() {
        return this.origin;
    }

    public CellValue evaluateFormula(Cell cell) {
        return getEvaluator().evaluate(cell);
    }

    private FormulaEvaluator getEvaluator() {
        if (this.evaluator == null) {
            this.evaluator = this.origin.getCreationHelper().createFormulaEvaluator();
        }
        return this.evaluator;
    }

    public ExcelSheet cloneSheet(int sourceSheetIndex, String sheetName) {
        Sheet sheet = this.origin.cloneSheet(sourceSheetIndex);
        int newSheetIndex = this.origin.getSheetIndex(sheet);
        this.origin.setSheetName(newSheetIndex, sheetName);
        return new ExcelSheet(this, sheet);
    }

    public ExcelSheet getSheetAt(int sheetIndex) {
        Sheet sheet = this.origin.getSheetAt(sheetIndex);
        return sheet == null ? null : new ExcelSheet(this, sheet);
    }

    public void setActiveSheet(int index) {
        this.origin.setActiveSheet(index);
    }

    public void removeSheetAt(int index) {
        this.origin.removeSheetAt(index);
    }

    public void close() throws IOException {
        this.origin.close();
    }

    public void write(OutputStream stream) throws IOException {
        this.origin.write(stream);
    }

    public CellStyle createCellStyle(String name) {
        CellStyle style = this.origin.createCellStyle();
        if (name != null) {
            setCellStyleName(style, name);
        }
        return style;
    }

    public CellStyle createCellStyle(String name, CellStyle baseStyle, Consumer<CellStyle> consumer) {
        CellStyle style = createCellStyle(name);
        if (baseStyle != null) {
            style.cloneStyleFrom(baseStyle);
        }
        consumer.accept(style);
        return style;
    }

    public void setCellStyleName(CellStyle style, String name) {
        this.styles.put(name, style);
    }

    public CellStyle getCellStyle(String name) {
        return this.styles.get(name);
    }

    public CellStyle getCellStyle(int sheetIndex, int rowIndex, int columnIndex) {
        Sheet sheet = this.origin.getSheetAt(sheetIndex);
        if (sheet != null) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    return cell.getCellStyle();
                }
            }
        }
        return null;
    }

    public Font createFont(String name) {
        Font font = this.origin.createFont();
        if (name != null) {
            setFontName(font, name);
        }
        return font;
    }

    public Font createFont(String name, HSSFFont baseFont, Consumer<Font> consumer) {
        Font font = createFont(name);
        if (baseFont != null) {
            ExcelExportUtil.cloneFont(baseFont, font);
        }
        consumer.accept(font);
        return font;
    }

    public void setFontName(Font font, String name) {
        this.fonts.put(name, font);
    }

    public Font getFont(String name) {
        return this.fonts.get(name);
    }

    public Font getFont(int sheetIndex, int rowIndex, int columnIndex) {
        CellStyle style = getCellStyle(sheetIndex, rowIndex, columnIndex);
        if (style instanceof HSSFCellStyle) {
            return ((HSSFCellStyle) style).getFont(this.origin);
        } else if (style instanceof XSSFCellStyle) {
            return ((XSSFCellStyle) style).getFont();
        }
        return null;
    }

    public void forEach(Consumer<ExcelSheet> consumer) {
        forEach(consumer, 0);
    }

    public void forEach(Consumer<ExcelSheet> consumer, int startIndex) {
        int size = this.origin.getNumberOfSheets();
        for (int i = startIndex; i < size; i++) {
            Sheet sheet = this.origin.getSheetAt(i);
            consumer.accept(new ExcelSheet(this, sheet));
        }
    }

}
