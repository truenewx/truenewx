package org.truenewx.tnxjeex.doc.excel;

import org.apache.poi.ss.util.CellAddress;

/**
 * Excel单元格格式异常
 *
 * @author jianglei
 */
public class ExcelCellFormatException extends ExcelCellException {

    private static final long serialVersionUID = 9124042701976169322L;

    public static final String EXPECTED_TYPE_NUMBER = "number";
    public static final String EXPECTED_TYPE_DATE = "date";
    public static final String EXPECTED_TYPE_MONTH = "month";
    public static final String EXPECTED_TYPE_PERMANENTABLE_DATE = "permanentable_date";

    private String originalText;

    /**
     * 构建无法读取的异常
     *
     * @param address 单元格地址
     */
    public ExcelCellFormatException(CellAddress address) {
        super(address, "error.tnxjeex.doc.excel.cell.unreadable");
    }

    /**
     * 构建格式错误的异常
     *
     * @param address      单元格地址
     * @param expectedType 期望类型
     * @param originalText 初始文本
     * @param args         格式化参数
     */
    public ExcelCellFormatException(CellAddress address, String expectedType, String originalText, Object... args) {
        super(address, "error.tnxjeex.doc.excel.cell.format_error_" + expectedType, args);
        this.originalText = originalText;
    }

    public String getOriginalText() {
        return this.originalText;
    }

    @Override
    public String getCode() {
        String code = super.getCode();
        if (getAddress() == null) {
            code += "_current";
        }
        return code;
    }

    @Override
    public Object[] getArgs() {
        Object[] args = super.getArgs();
        if (getAddress() == null) {
            return args;
        }
        Object[] mergedArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, mergedArgs, 1, args.length);
        mergedArgs[0] = getAddress().formatAsString();
        return mergedArgs;
    }

}
