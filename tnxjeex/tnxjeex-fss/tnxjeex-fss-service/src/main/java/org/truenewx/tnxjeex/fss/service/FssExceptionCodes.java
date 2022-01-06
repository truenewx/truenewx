package org.truenewx.tnxjeex.fss.service;

/**
 * 文件存储异常代码类
 *
 * @author jianglei
 */
public class FssExceptionCodes {

    private FssExceptionCodes() {
    }

    /**
     * 存储类型无对应的访问策略
     */
    public static final String NO_ACCESS_STRATEGY_FOR_TYPE = "error.fss.no_access_strategy_for_type";

    /**
     * 没有写权限
     */
    public static final String NO_WRITE_AUTHORITY = "error.fss.no_write_authority";

    /**
     * 没有读权限
     */
    public static final String NO_READ_AUTHORITY = "error.fss.no_read_authority";

    /**
     * 没有删除权限
     */
    public static final String NO_DELETE_AUTHORITY = "error.fss.no_delete_authority";

    /**
     * 未指定扩展名
     */
    public static final String NO_EXTENSION = "error.fss.no_extension";

    /**
     * 根据业务范围无法生成存储文件名，因此无法复制
     */
    public static final String CANNOT_COPY_WITHOUT_STORAGE_FILENAME_BY_SCOPE = "error.fss.cannot_copy_without_storage_filename_by_scope";

    /**
     * 不能在文件存储服务提供商之间复制
     */
    public static final String CANNOT_COPY_BETWEEN_PROVIDERS = "error.fss.cannot_copy_between_providers";

}
