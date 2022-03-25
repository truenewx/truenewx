package org.truenewx.tnxjeex.doc.excel.display;

import java.util.List;

/**
 * 展示时的Excel工作表摘要
 */
public class DisplayingExcelSheetSummary {

    private List<DisplayingExcelColumnModel> columns;
    private int rowNum;

    public DisplayingExcelSheetSummary(List<DisplayingExcelColumnModel> columns, int rowNum) {
        this.columns = columns;
        this.rowNum = rowNum;
    }

    public List<DisplayingExcelColumnModel> getColumns() {
        return this.columns;
    }

    public int getRowNum() {
        return this.rowNum;
    }

}
