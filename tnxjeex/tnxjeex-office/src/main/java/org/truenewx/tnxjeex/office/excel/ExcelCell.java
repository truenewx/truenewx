package org.truenewx.tnxjeex.office.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * Excel单元格
 *
 * @author jianglei
 */
public class ExcelCell {

    private Cell origin;
    private ExcelRow row;

    public ExcelCell(ExcelRow row, Cell origin) {
        this.origin = origin;
        this.row = row;
    }

    public Cell getOrigin() {
        return this.origin;
    }

    public ExcelRow getRow() {
        return this.row;
    }

    public int getColumnIndex() {
        return this.origin.getColumnIndex();
    }

    public void setCellValue(Object value) {
        if (value instanceof Number) {
            this.origin.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Temporal) {
            this.origin.setCellValue(TemporalUtil.format((Temporal) value));
        } else {
            if (value == null) {
                value = Strings.EMPTY;
            }
            this.origin.setCellValue(value.toString());
        }
    }

    public String getStringCellValue() {
        try {
            if (this.origin.getCellType() == CellType.NUMERIC) {
                try {
                    BigDecimal decimal = BigDecimal.valueOf(this.origin.getNumericCellValue());
                    return MathUtil.toShortestString(decimal);
                } catch (Exception ignored) {
                }
            }
            return this.origin.getStringCellValue();
        } catch (Exception e) {
            throw new ExcelCellException(this.origin.getAddress(), ExcelExceptionCodes.CELL_STRING_FORMAT_ERROR);
        }
    }

    public BigDecimal getNumericCellValue() {
        CellType cellType = this.origin.getCellType();
        if (cellType == CellType.NUMERIC) {
            try {
                return BigDecimal.valueOf(this.origin.getNumericCellValue());
            } catch (Exception ignored) {
            }
        } else if (cellType == CellType.FORMULA) {
            try {
                CellValue cellValue = this.row.getSheet().getDoc().evaluateFormula(this.origin);
                if (cellValue != null) {
                    return BigDecimal.valueOf(cellValue.getNumberValue());
                }
                return null;
            } catch (Exception ignored) {
            }
        }
        try {
            String value = this.origin.getStringCellValue();
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value);
            }
        } catch (Exception e) {
            throw new ExcelCellException(this.origin.getAddress(), ExcelExceptionCodes.CELL_NUMBER_FORMAT_ERROR);
        }
        return null;
    }

    public LocalDate getLocalDateCellValue() {
        if (this.origin.getCellType() == CellType.NUMERIC) {
            LocalDateTime dateTime = this.origin.getLocalDateTimeCellValue();
            if (dateTime != null) {
                return dateTime.toLocalDate();
            }
        }
        String value = this.origin.getStringCellValue();
        if (StringUtils.isNotBlank(value)) {
            LocalDate date = null;
            if (value.contains(Strings.MINUS)) {
                date = TemporalUtil.parseDate(value);
                if (date == null) {
                    date = TemporalUtil.parse(LocalDate.class, value, "yyyy-M-d");
                }
            } else if (value.contains(Strings.SLASH)) {
                date = TemporalUtil.parse(LocalDate.class, value, "yyyy/MM/dd");
                if (date == null) {
                    date = TemporalUtil.parse(LocalDate.class, value, "yyyy/M/d");
                }
            }
            if (date == null) {
                throw new ExcelCellException(this.origin.getAddress(), ExcelExceptionCodes.CELL_DATE_FORMAT_ERROR);
            }
            return date;
        }
        return null;
    }

    public LocalDate getLocalMonthCellValue() {
        String value = getStringCellValue();
        if (StringUtils.isNotBlank(value)) {
            LocalDate month = TemporalUtil.parseDate(value + "-01");
            if (month == null) {
                month = TemporalUtil.parse(LocalDate.class, value + "-1", "yyyy-M-d");
            }
            if (month == null) {
                throw new ExcelCellException(this.origin.getAddress(), ExcelExceptionCodes.CELL_MONTH_FORMAT_ERROR);
            }
            return month;
        }
        return null;
    }

    public void setCellStyle(HSSFCellStyle style) {
        this.origin.setCellStyle(style);
    }

    public CellStyle getCellStyle() {
        return this.origin.getCellStyle();
    }

    public void formatStringValue(Object... args) {
        String value = getStringCellValue();
        setCellValue(String.format(value, args));
    }

}
