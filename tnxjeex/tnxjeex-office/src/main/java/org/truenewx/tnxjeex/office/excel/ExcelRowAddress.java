package org.truenewx.tnxjeex.office.excel;

import java.util.Objects;

/**
 * Excel行的地址
 *
 * @author jianglei
 */
public class ExcelRowAddress {

    private String sheetName;
    private int sheetIndex;
    private int rowIndex;

    public ExcelRowAddress(String sheetName, int sheetIndex, int rowIndex) {
        this.sheetName = sheetName;
        this.sheetIndex = sheetIndex;
        this.rowIndex = rowIndex;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public int getSheetIndex() {
        return this.sheetIndex;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExcelRowAddress that = (ExcelRowAddress) o;
        return this.sheetIndex == that.sheetIndex && this.rowIndex == that.rowIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sheetIndex, this.rowIndex);
    }

}
