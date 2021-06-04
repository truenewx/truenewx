package org.truenewx.tnxjeex.office.excel;

/**
 * Excel导入异常错误码集
 *
 * @author jianglei
 */
public class ExcelExceptionCodes {

    private ExcelExceptionCodes() {
    }

    /**
     * 单元格字符串格式错误
     */
    public static final String CELL_STRING_FORMAT_ERROR = "tnxjeex.office.excel.cell.string_format_error";

    /**
     * 单元格数字格式错误
     */
    public static final String CELL_NUMBER_FORMAT_ERROR = "tnxjeex.office.excel.cell.number_format_error";

    /**
     * 单元格日期格式错误
     */
    public static final String CELL_DATE_FORMAT_ERROR = "tnxjeex.office.excel.cell.date_format_error";

    /**
     * 单元格月份格式错误
     */
    public static final String CELL_MONTH_FORMAT_ERROR = "tnxjeex.office.excel.cell.month_format_error";

    /**
     * 导入：单元格必填
     */
    public static final String IMPORT_CELL_REQUIRED = "tnxjeex.office.excel.import.cell_required";

    /**
     * 导入：单元格枚举显示名称错误
     */
    public static final String IMPORT_CELL_ENUM_ERROR = "tnxjeex.office.excel.import.cell_enum_error";

    /**
     * 导入：单元格行政区划显示名称错误
     */
    public static final String IMPORT_CELL_REGION_ERROR = "tnxjeex.office.excel.import.cell_region_error";

    /**
     * 导入：不支持的字段类型
     */
    public static final String IMPORT_SUPPORTED_FIELD_TYPE = "tnxjeex.office.excel.import_supported_field_type";


}
