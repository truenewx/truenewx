package org.truenewx.tnxjeex.doc.excel.display;

/**
 * 展示时的Excel列的数据模型
 */
public class DisplayingExcelColumnModel {

    /**
     * A1引用样式的列地址，如：A、B、AB等
     */
    private String address;
    /**
     * 列宽像素值
     */
    private int width;
    private Boolean hidden;

    public DisplayingExcelColumnModel(String address, int width, boolean hidden) {
        this.address = address;
        this.width = width;
        this.hidden = hidden ? Boolean.TRUE : null;
    }

    public String getAddress() {
        return this.address;
    }

    public int getWidth() {
        return this.width;
    }

    public Boolean getHidden() {
        return this.hidden;
    }

}
