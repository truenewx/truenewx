package org.truenewx.tnxjeex.file.office.excel.display;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * 展示时的Excel单元格的数据模型
 */
public class DisplayingExcelCellModel {

    private String value;
    private CellStyle style;
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

    public DisplayingExcelCellModel(String value, CellStyle style, Integer rowspan, Integer colspan) {
        this.value = value;
        this.style = style;
        this.rowspan = rowspan;
        this.colspan = colspan;
    }

    public String getValue() {
        return this.value;
    }

    public CellStyle getStyle() {
        return this.style;
    }

    public Integer getRowspan() {
        return this.rowspan;
    }

    public Integer getColspan() {
        return this.colspan;
    }

}
