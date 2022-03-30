package org.truenewx.tnxjeex.doc.excel;

import org.truenewx.tnxjeex.doc.core.DocOutline;
import org.truenewx.tnxjeex.doc.excel.display.DisplayingExcelCellStyle;

/**
 * Excel文档纲要
 *
 * @author jianglei
 */
public class ExcelDocOutline extends DocOutline {

    private DisplayingExcelCellStyle defaultStyle;

    public DisplayingExcelCellStyle getDefaultStyle() {
        return this.defaultStyle;
    }

    public void setDefaultStyle(DisplayingExcelCellStyle defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

}
