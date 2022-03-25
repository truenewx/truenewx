package org.truenewx.tnxjeex.doc.excel.display;

import org.apache.poi.ss.usermodel.Font;

/**
 * 展示时的Excel单元格样式
 */
public class DisplayingExcelCellStyle {

    /**
     * 下划线：单下划线
     */
    public static final String UNDERLINE_SINGLE = "SINGLE";
    /**
     * 下划线：双下划线
     */
    public static final String UNDERLINE_DOUBLE = "DOUBLE";

    /**
     * 下划线：会计用单下划线
     */
    public static final String UNDERLINE_SINGLE_ACCOUNTING = "SINGLE_ACCOUNTING";
    /**
     * 下划线：会计用双下划线
     */
    public static final String UNDERLINE_DOUBLE_ACCOUNTING = "DOUBLE_ACCOUNTING";

    /**
     * 偏移：上标
     */
    public static final String OFFSET_SUP = "SUP";
    /**
     * 偏移：下标
     */
    public static final String OFFSET_SUB = "SUB";

    private String alignment;
    private String verticalAlignment;
    private String backgroundColor;
    private String fontColor;
    private String fontName;
    private Short fontSizePt;
    private Boolean bold;
    private Boolean italic;
    private Boolean strikeout;
    private String underline;
    private String offset;

    public String getAlignment() {
        return this.alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Short getFontSizePt() {
        return this.fontSizePt;
    }

    public void setFontSizePt(Short fontSizePt) {
        this.fontSizePt = fontSizePt;
    }

    public Boolean getBold() {
        return this.bold;
    }

    public void setBold(Boolean bold) {
        if (bold == Boolean.TRUE) {
            this.bold = Boolean.TRUE;
        } else {
            this.bold = null;
        }
    }

    public Boolean getItalic() {
        return this.italic;
    }

    public void setItalic(Boolean italic) {
        if (italic == Boolean.TRUE) {
            this.italic = Boolean.TRUE;
        } else {
            this.italic = null;
        }
    }

    public Boolean getStrikeout() {
        return this.strikeout;
    }

    public void setStrikeout(Boolean strikeout) {
        if (strikeout == Boolean.TRUE) {
            this.strikeout = Boolean.TRUE;
        } else {
            this.strikeout = null;
        }
    }

    public String getUnderline() {
        return this.underline;
    }

    public void setUnderline(String underline) {
        this.underline = underline;
    }

    public String getOffset() {
        return this.offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    //////

    public void setUnderline(byte underline) {
        switch (underline) {
            case Font.U_SINGLE:
                this.underline = UNDERLINE_SINGLE;
                break;
            case Font.U_DOUBLE:
                this.underline = UNDERLINE_DOUBLE;
                break;
            case Font.U_SINGLE_ACCOUNTING:
                this.underline = UNDERLINE_SINGLE_ACCOUNTING;
                break;
            case Font.U_DOUBLE_ACCOUNTING:
                this.underline = UNDERLINE_DOUBLE_ACCOUNTING;
                break;
            default:
                break;
        }
    }

    public void setOffset(short offset) {
        switch (offset) {
            case Font.SS_SUPER:
                this.offset = OFFSET_SUP;
                break;
            case Font.SS_SUB:
                this.offset = OFFSET_SUB;
                break;
            default:
                break;
        }
    }
}
