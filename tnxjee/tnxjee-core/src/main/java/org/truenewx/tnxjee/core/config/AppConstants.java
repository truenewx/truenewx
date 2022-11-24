package org.truenewx.tnxjee.core.config;

/**
 * 应用常量类
 */
public class AppConstants {


    private AppConstants() {
    }

    /**
     * 属性：Spring应用名称
     */
    public static final String PROPERTY_SPRING_APP_NAME = "spring.application.name";

    /**
     * 表达式：Spring应用名称
     */
    public static final String EL_SPRING_APP_NAME = "${spring.application.name}";

    public static final String PROPERTY_SERVER_PORT = "server.port";

    public static final int DEFAULT_SERVER_PORT = 8080;

    /**
     * 属性：框架应用标识符
     */
    public static final String PROPERTY_FRAMEWORK_APP_SYMBOL = "application.symbol";

    /**
     * 表达式：框架应用标识符
     */
    public static final String EL_FRAMEWORK_APP_SYMBOL = "${application.symbol}";

}
