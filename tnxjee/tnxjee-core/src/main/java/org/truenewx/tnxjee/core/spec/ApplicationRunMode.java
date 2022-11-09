package org.truenewx.tnxjee.core.spec;

/**
 * 应用运行方式
 */
public enum ApplicationRunMode {

    /**
     * 在IDE中，上下文环境为源代码
     */
    IDE,
    /**
     * 以java -jar方式启动，上下文环境为jar/war包所在目录
     */
    JAR,
    /**
     * 在Tomcat容器中运行，上下文环境为Tomcat安装目录
     */
    TOMCAT,

}
