package org.truenewx.tnxjeex.file.office.excel;

import org.truenewx.tnxjee.core.Strings;

/**
 * Excel单元格格式
 */
public class ExcelCellFormat {

    private ExcelCellFormatType type;
    private String pattern;

    public ExcelCellFormat(ExcelCellFormatType type, String pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    public ExcelCellFormatType getType() {
        return this.type;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public String toString() {
        return this.type + Strings.COLON + Strings.SPACE + this.pattern;
    }

}
