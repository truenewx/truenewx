package org.truenewx.tnxjeex.office.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
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
        BigDecimal decimal = readDecimalValue();
        if (decimal != null) {
            return MathUtil.toShortestString(decimal);
        }
        return readStringValue();
    }

    /**
     * 以数字格式读取单元格内容
     *
     * @return 单元格内容数字，未成功读取时返回null
     */
    private BigDecimal readDecimalValue() {
        try {
            CellType cellType = this.origin.getCellType();
            if (cellType == CellType.NUMERIC) {
                return BigDecimal.valueOf(this.origin.getNumericCellValue());
            } else if (cellType == CellType.FORMULA) {
                CellValue cellValue = this.row.getSheet().getDoc().evaluateFormula(this.origin);
                if (cellValue != null) {
                    return BigDecimal.valueOf(cellValue.getNumberValue());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 以字符串格式读取单元格内容
     *
     * @return 单元格内容字符串
     */
    private String readStringValue() {
        try {
            return this.origin.getStringCellValue();
        } catch (Exception e) {
            throw new ExcelCellFormatException(this.origin.getAddress());
        }
    }

    public BigDecimal getNumericCellValue() {
        BigDecimal decimal = readDecimalValue();
        if (decimal != null) {
            return decimal;
        }

        String text = readStringValue();
        if (StringUtils.isNotBlank(text)) {
            try {
                return new BigDecimal(text);
            } catch (NumberFormatException e) { // 可以读取字符串但数字格式化异常，则抛出数字格式错误异常
                throw new ExcelCellFormatException(this.origin.getAddress(),
                        ExcelCellFormatException.EXPECTED_TYPE_NUMBER, text);
            }
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
        String text = readStringValue();
        if (StringUtils.isNotBlank(text)) {
            LocalDate date = null;
            if (text.contains(Strings.MINUS)) {
                date = TemporalUtil.parseDate(text);
                if (date == null) {
                    date = TemporalUtil.parse(LocalDate.class, text, "yyyy-M-d");
                }
            } else if (text.contains(Strings.SLASH)) {
                date = TemporalUtil.parse(LocalDate.class, text, "yyyy/MM/dd");
                if (date == null) {
                    date = TemporalUtil.parse(LocalDate.class, text, "yyyy/M/d");
                }
            }
            if (date == null) {
                throw new ExcelCellFormatException(this.origin.getAddress(),
                        ExcelCellFormatException.EXPECTED_TYPE_DATE, text);
            }
            return date;
        }
        return null;
    }

    public LocalDate getLocalMonthCellValue() {
        String text = getStringCellValue();
        if (StringUtils.isNotBlank(text)) {
            LocalDate month = TemporalUtil.parseDate(text + "-01");
            if (month == null) {
                month = TemporalUtil.parse(LocalDate.class, text + "-1", "yyyy-M-d");
            }
            if (month == null) {
                throw new ExcelCellFormatException(this.origin.getAddress(),
                        ExcelCellFormatException.EXPECTED_TYPE_MONTH, text);
            }
            return month;
        }
        return null;
    }

    public PermanentableDate getPermanentableDateCellValue(Supplier<String> permanentDateTextSupplier) {
        try {
            LocalDate date = getLocalDateCellValue();
            return date == null ? null : new PermanentableDate(date);
        } catch (ExcelCellFormatException e) { // 不是正确的日期格式，则判断是否表示永久
            String text = getStringCellValue();
            if (StringUtils.isNotBlank(text)) {
                String permanentDateText = permanentDateTextSupplier.get();
                if (text.equals(permanentDateText)) { // 匹配表示永久的文本，则返回永久日期
                    return PermanentableDate.ofPermanent();
                }
                // 不匹配则抛出格式异常
                throw new ExcelCellFormatException(this.origin.getAddress(),
                        ExcelCellFormatException.EXPECTED_TYPE_PERMANENTABLE_DATE, text, permanentDateText);
            }
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
