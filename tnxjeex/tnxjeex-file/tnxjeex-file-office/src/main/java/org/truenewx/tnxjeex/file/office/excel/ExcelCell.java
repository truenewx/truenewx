package org.truenewx.tnxjeex.file.office.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.CellRangeAddress;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.TemporalUtil;
import org.truenewx.tnxjeex.file.office.excel.display.ExcelDisplayUtil;

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
        String pattern = getDatePattern();
        if (pattern != null) {
            Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(this.origin.getNumericCellValue());
            return date == null ? null : DateUtil.format(date, pattern);
        }
        if (this.origin.getCellType() == CellType.FORMULA) {
            CellValue cellValue = this.row.getSheet().getDoc().evaluateFormula(this.origin);
            if (cellValue != null) {
                return cellValue.formatAsString();
            }
        }
        BigDecimal decimal = readDecimalValue();
        if (decimal != null) {
            return MathUtil.toShortestString(decimal);
        }
        return readStringValue();
    }

    public boolean isDateFormatted() {
        CellType cellType = this.origin.getCellType();
        if (cellType != CellType.NUMERIC && cellType != CellType.FORMULA) {
            return false;
        }
        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(this.origin)) {
            return true;
        }
        short dataFormat = this.origin.getCellStyle().getDataFormat();
        return dataFormat == 177 || dataFormat == 180;
    }

    public String getDatePattern() {
        if (isDateFormatted()) {
            CellStyle style = this.origin.getCellStyle();
            short dataFormat = style.getDataFormat();
            String pattern = ExcelDisplayUtil.getConfiguredDatePattern(dataFormat);
            if (pattern != null) {
                return pattern;
            }
            pattern = style.getDataFormatString();
            // 去掉类似：[$-x-sysdate]、[$-x-systime]、[DBNum1][$-zh-CN]这样的开头
            if (pattern.startsWith(Strings.LEFT_SQUARE_BRACKET)) {
                int index = pattern.lastIndexOf(Strings.RIGHT_SQUARE_BRACKET);
                if (index > 0) {
                    pattern = pattern.substring(index + 1);
                }
            }
            // 去掉;@的结尾
            if (pattern.endsWith(";@")) {
                pattern = pattern.substring(0, pattern.length() - 2);
            }
            // 转义符替换
            pattern = pattern.replaceAll("\\\\ ", Strings.SPACE).replaceAll("\\\\,", Strings.COMMA);
            // 去掉可能的引号
            pattern = pattern.replaceAll("\"", Strings.EMPTY);
            // 表示星期几的a替换为E
            pattern = pattern.replaceAll("a", "E");
            // Excel中小写m可表示分钟也可以表示月份，需根据上下文找出表示月份的m，转换为大写的M，用于在Java中表示月份
            // 连续的4个或3个m，均表示月份
            pattern = pattern.replaceAll("mmmm", "MMMM").replaceAll("mmm", "MMM");
            // 此时pattern中只可能存在m或mm，不存在更多连续的m
            // m默认表示月份，在h之后的首个m表示分钟，不在h之前但在s之前的最后一个m表示分钟，s前面没有m，则后面的第一个m为表示分钟
            // 这样的正则表达式过于复杂，可读性差难以维护，用代码算法实现替换为
            StringBuilder sb = new StringBuilder(pattern);
            for (int i = 0; i < sb.length(); i++) {
                if (isMonthPatternChar(sb, i)) {
                    sb.setCharAt(i, 'M');
                    // 下一个字符也是m，则同样替换，并跳过
                    if (i < sb.length() - 1 && sb.charAt(i + 1) == 'm') {
                        sb.setCharAt(i + 1, 'M');
                        i++;
                    }
                }
            }
            // 如果格式中存在am/pm，则其中的h为12小时制，否则为24小时制
            pattern = sb.toString();
            if (pattern.toLowerCase().contains("am/pm")) {
                pattern = pattern.replaceAll("am/pm", "a").replaceAll("AM/PM", "a");
            } else {
                pattern = pattern.replaceAll("h", "H");
            }
            return pattern;
        }
        return null;
    }

    /**
     * 判断指定格式中指定位置的字符是否表示月份
     *
     * @param pattern 格式
     * @param index   字符索引位置
     * @return 是否表示月份
     */
    private boolean isMonthPatternChar(StringBuilder pattern, int index) {
        char c = pattern.charAt(index);
        if (c == 'm') {
            String m = "m";
            String h = "h";
            String s = "s";

            // 如果后面有h，则一定是月份
            int hIndex = pattern.indexOf(h, index + 1);
            if (hIndex > index) {
                return true;
            }
            // h之后的首个m为分钟
            String prefix = pattern.substring(0, index);
            hIndex = prefix.lastIndexOf(h);
            if (hIndex >= 0) {
                // h和m之间没有其它非连续的m，则说明这个m是h之后的首个m，否则这个m就是月份
                int mIndex = prefix.lastIndexOf(m, hIndex + 1);
                return mIndex > hIndex && mIndex != index - 1;
            }
            // 此处m后面一定没有h，如果m为s前的最后一个m，则一定为分钟
            int sIndex = pattern.indexOf(s, index + 1);
            if (sIndex > index) {
                // 跳过后一个字符，因为后一个字符如果是m，则为连续mm，作为整体判断
                if (!pattern.substring(index + 2, sIndex).contains(m)) {
                    return false;
                }
            }
            // 此时，当前m之前如果有s，且为s后的首个m，则该s前如果有m，则当前m为月份，否则为分钟
            sIndex = prefix.lastIndexOf(s);
            if (sIndex == 0) { // s在首位，则s前肯定没有m，当前m一定为分钟
                return false;
            } else if (sIndex > 0) { // s不在首位，检查s前面有没有m
                return prefix.substring(0, sIndex).contains(m);
            }
            return true;
        }
        return false;
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
            String value = this.origin.getStringCellValue();
            return value == null ? null : value.trim(); // Excel单元格里容易出现不易察觉的空格，读取时去掉首尾空格
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
        if (this.origin.getCellType() == CellType.NUMERIC) {
            Date date = this.origin.getDateCellValue();
            if (date != null) {
                return TemporalUtil.toLocalDate(date.toInstant());
            }
        } else {
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

    /**
     * 判断当前单元格是否指定范围的第一个单元格
     *
     * @param rangeAddress 范围地址
     * @return 当前单元格是否指定范围的第一个单元格
     */
    public boolean isFirstIn(CellRangeAddress rangeAddress) {
        return this.origin.getRowIndex() == rangeAddress.getFirstRow()
                && this.origin.getColumnIndex() == rangeAddress.getFirstColumn();
    }

}
