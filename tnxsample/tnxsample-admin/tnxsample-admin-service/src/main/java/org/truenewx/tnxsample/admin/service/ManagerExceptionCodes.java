package org.truenewx.tnxsample.admin.service;

/**
 * 管理员模块业务异常错误码集
 *
 * @author jianglei
 */
public class ManagerExceptionCodes {

    private ManagerExceptionCodes() {
    }

    /**
     * 用户名或密码错误
     */
    public static final String USERNAME_OR_PASSWORD_ERROR =
            "error.manager.username_or_password_error";
    /**
     * 原密码错误
     */
    public static final String OLD_PASSWORD_ERROR = "error.manager.old_password_error";

    /**
     * 管理员被禁用
     */
    public static final String DISABLED_MANAGER = "error.manager.disabled_manager";

    /**
     * 工号重复
     */
    public static final String REPEAT_JOB_NO = "error.manager.repeat_job_no";

    /**
     * 用户名重复
     */
    public static final String REPEAT_USERNAME = "error.manager.repeat_username";

    /**
     * 角色名称重复
     */
    public static final String ROLE_REPEAT_NAME = "error.manager.role.repeat_name";

}
