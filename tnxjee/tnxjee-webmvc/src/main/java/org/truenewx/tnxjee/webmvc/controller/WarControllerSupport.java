package org.truenewx.tnxjee.webmvc.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.ApplicationUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.version.VersionReader;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * war包控制器支持
 */
public abstract class WarControllerSupport {

    @Autowired
    protected CommonProperties commonProperties;
    @Autowired
    protected VersionReader versionReader;

    public String getVersion() {
        return this.versionReader.getVersionText();
    }

    public Long getSize() {
        File sourceFile = ApplicationUtil.getWarFile();
        if (sourceFile.exists()) {
            return sourceFile.length();
        }
        return null;
    }

    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        File sourceFile = ApplicationUtil.getWarFile();
        if (sourceFile.exists()) {
            String filename = getDownloadFilename(request) + FileExtensions.DOT_WAR;
            WebUtil.download(request, response, filename, sourceFile);
        } else {
            response.getWriter().println("Source war file not found.");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * @param request http请求
     * @return 下载文件名（不含扩展名）
     */
    protected String getDownloadFilename(HttpServletRequest request) {
        String appName = SpringWebMvcUtil.getApplicationName(request);
        String filename = appName;
        AppConfiguration app = this.commonProperties.getApp(appName);
        if (app != null) {
            String symbol = app.getSymbol();
            if (StringUtils.isNotBlank(symbol)) {
                filename = symbol;
            }
        }
        filename += Strings.MINUS + getVersion();
        return filename;
    }

}
