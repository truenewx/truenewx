package org.truenewx.tnxjeex.doc.msoffice.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;
import org.truenewx.tnxjeex.doc.core.DocOutline;
import org.truenewx.tnxjeex.doc.core.DocOutlineItem;
import org.truenewx.tnxjeex.doc.core.util.DocUtil;
import org.truenewx.tnxjeex.doc.msoffice.excel.display.DisplayingExcelCellStyle;
import org.truenewx.tnxjeex.doc.msoffice.excel.exports.ExcelExportUtil;

/**
 * Excel文档
 *
 * @author jianglei
 */
public class ExcelDoc {

    private String filename;
    private Workbook origin;
    private Map<String, CellStyle> styles = new HashMap<>();
    private Map<String, Font> fonts = new HashMap<>();
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;
    private DisplayingExcelCellStyle defaultDisplayStyle;

    public ExcelDoc(String extension) {
        extension = DocUtil.standardizeExtension(extension);
        if (FileExtensions.XLS.equalsIgnoreCase(extension)) {
            this.origin = new HSSFWorkbook();
        } else {
            this.origin = new XSSFWorkbook();
        }
    }

    public ExcelDoc(InputStream in, String extension) {
        extension = DocUtil.standardizeExtension(extension);
        try {
            if (FileExtensions.XLS.equalsIgnoreCase(extension)) {
                this.origin = new HSSFWorkbook(in);
            } else {
                this.origin = new XSSFWorkbook(in);
            }
        } catch (IOException e) {
            throw new BusinessException(DocExceptionCodes.CAN_NOT_LOAD, extension);
        }
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Workbook getOrigin() {
        return this.origin;
    }

    public DocOutline getOutline(boolean includingHidden) {
        ExcelDocOutline outline = new ExcelDocOutline();
        List<DocOutlineItem> items = new ArrayList<>();
        int sheetNum = this.origin.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            if (includingHidden || !this.origin.isSheetHidden(i)) {
                Sheet sheet = this.origin.getSheetAt(i);
                DocOutlineItem item = new DocOutlineItem();
                item.setCaption(sheet.getSheetName());
                // 前面可能有被隐藏的sheet，索引严格按照加入顺序编排
                item.setPageIndex(items.size());
                items.add(item);
                if (sheet.isSelected()) {
                    outline.setSelectedItemIndexes(List.of(item.getPageIndex()));
                }
            }
        }
        outline.setItems(items);
        outline.setPageCount(items.size());
        outline.setDefaultStyle(getDefaultDisplayStyle());
        return outline;
    }

    public CellValue evaluateFormula(Cell cell) {
        return getFormulaEvaluator().evaluate(cell);
    }

    private FormulaEvaluator getFormulaEvaluator() {
        if (this.formulaEvaluator == null) {
            this.formulaEvaluator = this.origin.getCreationHelper().createFormulaEvaluator();
        }
        return this.formulaEvaluator;
    }

    public String formatCellValue(Cell cell) {
        if (this.dataFormatter == null) {
            this.dataFormatter = new DataFormatter();
        }
        if (cell.getCellType() == CellType.FORMULA) {
            return this.dataFormatter.formatCellValue(cell, getFormulaEvaluator());
        } else {
            return this.dataFormatter.formatCellValue(cell);
        }

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

    public void close() {
        try {
            this.origin.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
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

    public Font createFont(String name, Font baseFont, Consumer<Font> consumer) {
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
        return getFont(style);
    }

    public Font getFont(CellStyle style) {
        if (style instanceof HSSFCellStyle) {
            return ((HSSFCellStyle) style).getFont(this.origin);
        } else if (style instanceof XSSFCellStyle) {
            return ((XSSFCellStyle) style).getFont();
        }
        return null;
    }

    public Color getFontColor(Font font) {
        if (font instanceof XSSFFont) {
            return ((XSSFFont) font).getXSSFColor();
        } else if (font instanceof HSSFFont) {
            return ((HSSFFont) font).getHSSFColor((HSSFWorkbook) this.origin);
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

    /**
     * @return 当前文档默认展示样式
     */
    public DisplayingExcelCellStyle getDefaultDisplayStyle() {
        if (this.defaultDisplayStyle == null) {
            CellStyle style = this.origin.getCellStyleAt(0);
            DisplayingExcelCellStyle displayStyle = new DisplayingExcelCellStyle();
            // 默认展示样式只包含垂直对齐、字体名称、字体大小
            displayStyle.setVerticalAlignment(style.getVerticalAlignment().name());
            Font font = getFont(style);
            displayStyle.setFontName(font.getFontName());
            displayStyle.setFontSizePt(font.getFontHeightInPoints());
            this.defaultDisplayStyle = displayStyle;
        }
        return this.defaultDisplayStyle;
    }

}
