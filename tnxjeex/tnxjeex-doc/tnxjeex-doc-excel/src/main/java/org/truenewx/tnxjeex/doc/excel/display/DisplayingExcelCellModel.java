package org.truenewx.tnxjeex.doc.excel.display;

/**
 * 展示时的Excel单元格的数据模型
 */
public class DisplayingExcelCellModel {

    private String value;
    private DisplayingExcelCellStyle style;
    /**
     * 合并单元格时占用的行数
     */
    private Integer rowspan;
    /**
     * 合并单元格时占用的列数
     */
    private Integer colspan;

    public DisplayingExcelCellModel() {
    }

    public DisplayingExcelCellModel(String value, DisplayingExcelCellStyle style) {
        this();
        this.value = value;
        this.style = style;
    }

    public DisplayingExcelCellModel(String value, DisplayingExcelCellStyle style, int rowspan, int colspan) {
        this(value, style);
        this.rowspan = rowspan;
        this.colspan = colspan;
    }

    public String getValue() {
        return this.value;
    }

    public DisplayingExcelCellStyle getStyle() {
        return this.style;
    }

    public Integer getRowspan() {
        return this.rowspan;
    }

    public Integer getColspan() {
        return this.colspan;
    }

}
