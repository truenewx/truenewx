package org.truenewx.tnxsample.common.constant;

/**
 * 会话常量类
 *
 * @author jianglei
 */
public class SessionConstants {

    private SessionConstants() {
    }

    /**
     * 存放session信息的集合名称
     */
    public static final String SESSION_COLLECTION_NAME = "session";

    /**
     * session过期时间，单位：秒
     */
    public static final int SESSION_TIMEOUT = 900;

}
