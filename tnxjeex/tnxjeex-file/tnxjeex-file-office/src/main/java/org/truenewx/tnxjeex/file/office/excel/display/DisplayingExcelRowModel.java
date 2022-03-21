package org.truenewx.tnxjeex.file.office.excel.display;

/**
 * 展示时的Excel行的数据模型
 */
public class DisplayingExcelRowModel {

    /**
     * 行号
     */
    private int number;
    /**
     * 行高像素值
     */
    private int height;
    private Boolean hidden;
    private DisplayingExcelCellModel[] cells = new DisplayingExcelCellModel[0];

    public DisplayingExcelRowModel(int number, int height, boolean hidden) {
        this.number = number;
        this.height = height;
        this.hidden = hidden ? Boolean.TRUE : null;
    }

    public int getNumber() {
        return this.number;
    }

    public int getHeight() {
        return this.height;
    }

    public Boolean getHidden() {
        return this.hidden;
    }

    public DisplayingExcelCellModel[] getCells() {
        return this.cells;
    }

    public void setCells(DisplayingExcelCellModel[] cells) {
        this.cells = cells == null ? new DisplayingExcelCellModel[0] : cells;
    }

    public String[] getData() {
        String[] values = new String[this.cells.length];
        for (int i = 0; i < this.cells.length; i++) {
            DisplayingExcelCellModel cell = this.cells[i];
            if (cell != null) {
                values[i] = cell.getValue();
            }
        }
        return values;
    }

}
