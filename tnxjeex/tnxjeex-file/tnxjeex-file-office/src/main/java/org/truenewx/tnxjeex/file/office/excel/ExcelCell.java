package org.truenewx.tnxjeex.file.office.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.PermanentableDate;
import org.truenewx.tnxjee.core.util.LogUtil;
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

    /**
     * 执行公式
     *
     * @return 公式执行结果值
     */
    private CellValue evaluateFormula() {
        return this.row.getSheet().getDoc().evaluateFormula(this.origin);
    }

    /**
     * 以字符串形式加载单元格内容，无法加载时抛出{@link ExcelCellFormatException}
     *
     * @return 单元格内容字符串
     */
    private String readStringValue() {
        try {
            CellType cellType = this.origin.getCellType();
            if (cellType == CellType.STRING) {
                String value = this.origin.getRichStringCellValue().getString();
                return value == null ? null : value.trim(); // Excel单元格里容易出现不易察觉的空格，读取时去掉首尾空格
            } else if (cellType == CellType.FORMULA) {
                CellValue cellValue = evaluateFormula();
                if (cellValue != null) {
                    return cellValue.formatAsString();
                }
            } else if (cellType == CellType.BLANK) {
                return Strings.EMPTY;
            }
        } catch (Exception ignored) {
        }
        throw new ExcelCellFormatException(this.origin.getAddress());
    }

    /**
     * 以数字形式加载单元格内容，无法加载时抛出{@link ExcelCellFormatException}
     *
     * @return 单元格内容数字
     */
    private Double readNumberValue() {
        try {
            CellType cellType = this.origin.getCellType();
            if (cellType == CellType.NUMERIC) {
                return this.origin.getNumericCellValue();
            } else if (cellType == CellType.FORMULA) {
                CellValue cellValue = evaluateFormula();
                if (cellValue != null) {
                    return cellValue.getNumberValue();
                }
            } else if (cellType == CellType.BLANK) {
                return null;
            }
        } catch (Exception ignored) {
        }
        throw new ExcelCellFormatException(this.origin.getAddress());
    }

    private Date readDateValue() {
        Double number = readNumberValue();
        return number == null ? null : DateUtil.getJavaDate(number);
    }

    /**
     * @return 当前单元格的Java规格的日期时间格式
     */
    public String getDateTimePattern() {
        // 经测试发现，short类型的dataFormat在不同Excel文档中可能具有不同的值，不能用于作为判断依据，只能根据dataFormatString进行转换
        if (isDateTimeType()) {
            CellStyle style = this.origin.getCellStyle();
            String format = style.getDataFormatString();
            int index = format.indexOf("[$-");
            if (index >= 0) {
                format = format.substring(index, format.indexOf(Strings.RIGHT_SQUARE_BRACKET, index) + 1);
            }
            String pattern = ExcelUtil.getConfiguredDataPattern(format);
            if (pattern == null) {
                pattern = style.getDataFormatString();
                // 去掉类似：[$-x-sysdate]、[$-x-systime]、[DBNum1][$-zh-CN]这样的开头
                if (pattern.startsWith(Strings.LEFT_SQUARE_BRACKET)) {
                    int bracketIndex = pattern.lastIndexOf(Strings.RIGHT_SQUARE_BRACKET);
                    if (bracketIndex > 0) {
                        pattern = pattern.substring(bracketIndex + 1);
                    }
                }
                // 去掉;@的结尾
                if (pattern.endsWith(";@")) {
                    pattern = pattern.substring(0, pattern.length() - 2);
                }
                // 转义符替换
                pattern = pattern.replaceAll("\\\\ ", Strings.SPACE).replaceAll("\\\\,", Strings.COMMA);
                // 表示星期几的a替换为E
                pattern = pattern.replaceAll("a", "E");
                // 如果格式中存在英文或中文的AM/PM，则为12小时制，否则为24小时制
                String halfDayKey = "AM/PM";
                boolean halfDay = pattern.contains(halfDayKey);
                if (halfDay) {
                    pattern = pattern.replaceAll(halfDayKey, "a");
                } else {
                    halfDayKey = ExcelUtil.getConfiguredDataPattern(halfDayKey);
                    halfDay = pattern.contains(halfDayKey);
                    if (halfDay) {
                        pattern = pattern.replaceAll(halfDayKey, "a");
                    }
                }
                // /替换为-，但\/替换为/
                int slashIndex = pattern.indexOf(Strings.SLASH);
                while (slashIndex >= 0) {
                    if (slashIndex == 0) {
                        pattern = Strings.MINUS + pattern.substring(1);
                    } else {
                        if (pattern.charAt(slashIndex - 1) == '\\') {
                            pattern = pattern.substring(0, slashIndex - 1) + pattern.substring(slashIndex);
                        } else {
                            pattern = pattern.substring(0, slashIndex) + Strings.MINUS + pattern.substring(
                                    slashIndex + 1);
                        }
                    }
                    slashIndex = pattern.indexOf(Strings.SLASH, slashIndex + 1);
                }
                // 去掉双引号
                pattern = pattern.replaceAll(Strings.DOUBLE_QUOTES, Strings.EMPTY);
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
                pattern = sb.toString();
                // 如果格式为24小时制，需转换为大写H，到最后再进行该转换是为了便于m的转换过程中判断h
                if (!halfDay) {
                    pattern = pattern.replaceAll("h", "H");
                }
            }
            return pattern;
        }
        return null;
    }

    private boolean isDateTimeType() {
        CellType cellType = this.origin.getCellType();
        if (cellType != CellType.NUMERIC && cellType != CellType.FORMULA) {
            return false;
        }
        if (DateUtil.isCellDateFormatted(this.origin)) {
            return true;
        }
        String format = this.origin.getCellStyle().getDataFormatString();
        return format.endsWith(";@");
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
     * 获取转换为字符串的值
     *
     * @return 字符串值
     */
    public String getValueAsString() {
        // 日期时间类型的单元格用DataFormatter大概率无法正确处理，通过格式转换进行处理
        String dateTimePattern = getDateTimePattern();
        if (dateTimePattern != null) {
            Date date = readDateValue();
            try {
                return date == null ? null : org.truenewx.tnxjee.core.util.DateUtil.format(date, dateTimePattern);
            } catch (Exception e) {
                LogUtil.warn(getClass(), e.getMessage());
            }
        }
        try {
            return this.row.getSheet().getDoc().formatCellValue(this.origin);
        } catch (Exception e) {
            LogUtil.warn(getClass(), e.getMessage());
        }
        // 自定义转换和DataFormatter都无法正确处理的，用默认方式处理
        if (dateTimePattern != null) {
            // 日期时间类型默认转换为长时间格式
            Date date = readDateValue();
            return date == null ? null : org.truenewx.tnxjee.core.util.DateUtil.formatLong(date);
        }
        // 其它读取字符串值
        return readStringValue();
    }

    /**
     * 获取转换为十进制数字的值，如果无法转换则返回null
     *
     * @return 数字值
     */
    public BigDecimal getValueAsDecimal() {
        try {
            Double number = readNumberValue();
            return number == null ? null : BigDecimal.valueOf(number);
        } catch (ExcelCellFormatException e) {
            String text = readStringValue();
            if (StringUtils.isNotBlank(text)) {
                try {
                    return new BigDecimal(text);
                } catch (NumberFormatException ex) { // 可以读取字符串但数字格式化异常，则抛出数字格式错误异常
                    throw new ExcelCellFormatException(this.origin.getAddress(),
                            ExcelCellFormatException.EXPECTED_TYPE_NUMBER, text);
                }
            }
        }
        return null;
    }

    /**
     * 获取转换为日期的值，如果无法转换则返回null
     *
     * @return 日期值
     */
    public LocalDate getValueAsDate() {
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

    /**
     * 获取转换为表达月份的日期的值，如果无法转换则返回null
     *
     * @return 表达月份的日期值
     */
    public LocalDate getValueAsMonthDate() {
        if (this.origin.getCellType() == CellType.NUMERIC) {
            Date date = this.origin.getDateCellValue();
            if (date != null) {
                return TemporalUtil.toLocalDate(date.toInstant());
            }
        } else {
            String text = getValueAsString();
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

    /**
     * 获取转换为可表达永久的日期的值，如果无法转换则返回null
     *
     * @return 可表达永久的日期值
     */
    public PermanentableDate getValueAsPermanentableDate(Supplier<String> permanentDateTextSupplier) {
        try {
            LocalDate date = getValueAsDate();
            return date == null ? null : new PermanentableDate(date);
        } catch (ExcelCellFormatException e) { // 不是正确的日期格式，则判断是否表示永久
            String text = getValueAsString();
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

    public CellStyle getCellStyle(boolean display) {
        CellStyle cellStyle = this.origin.getCellStyle();
        // 用于展示的样式，在水平对齐为一般时，按照数据类型赋予不同的默认值
        if (display && cellStyle.getAlignment() == HorizontalAlignment.GENERAL) {
            HorizontalAlignment alignment;
            CellType cellType = this.origin.getCellType();
            if (cellType == CellType.FORMULA) {
                CellValue cellValue = evaluateFormula();
                alignment = ExcelUtil.getDefaultAlignment(cellValue.getCellType());
            } else {
                alignment = ExcelUtil.getDefaultAlignment(cellType);
            }
            cellStyle.setAlignment(alignment);
        }
        return cellStyle;
    }

    public void formatStringCellValue(Object... args) {
        String value = getValueAsString();
        setCellValue(String.format(value, args));
    }

}
