package org.truenewx.tnxjeex.doc.excel;

/**
 * Excel导入异常错误码集
 *
 * @author jianglei
 */
public class ExcelExceptionCodes {

    private ExcelExceptionCodes() {
    }

    /**
     * 导入：单元格必填
     */
    public static final String IMPORT_CELL_REQUIRED = "error.tnxjeex.doc.excel.import.cell_required";

    /**
     * 导入：单元格枚举显示名称错误
     */
    public static final String IMPORT_CELL_ENUM_ERROR = "error.tnxjeex.doc.excel.import.cell_enum_error";

    /**
     * 导入：单元格行政区划显示名称错误
     */
    public static final String IMPORT_CELL_REGION_ERROR = "error.tnxjeex.doc.excel.import.cell_region_error";

    /**
     * 导入：不支持的字段类型
     */
    public static final String IMPORT_SUPPORTED_FIELD_TYPE = "error.tnxjeex.doc.excel.import_supported_field_type";


}
