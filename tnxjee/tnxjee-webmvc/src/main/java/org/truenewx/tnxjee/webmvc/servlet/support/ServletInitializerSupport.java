package org.truenewx.tnxjee.webmvc.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.truenewx.tnxjee.core.spec.ApplicationRunMode;
import org.truenewx.tnxjee.core.util.ApplicationUtil;

/**
 * Servlet初始化器支持，用于在Servlet容器启动伊始执行预处理
 *
 * @author jianglei
 */
public abstract class ServletInitializerSupport extends SpringBootServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ApplicationUtil.RUN_MODE = ApplicationRunMode.TOMCAT;
        super.onStartup(servletContext);
    }

}
