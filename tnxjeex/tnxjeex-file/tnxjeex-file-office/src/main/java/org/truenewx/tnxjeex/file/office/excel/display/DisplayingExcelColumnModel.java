package org.truenewx.tnxjeex.file.office.excel.display;

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
    private boolean hidden;

    public DisplayingExcelColumnModel(String address, int width, boolean hidden) {
        this.address = address;
        this.width = width;
        this.hidden = hidden;
    }

    public String getAddress() {
        return this.address;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isHidden() {
        return this.hidden;
    }

}
