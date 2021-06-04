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
     * 未指定扩展名
     */
    public static final String NO_EXTENSION = "error.fss.no_extension";

}
