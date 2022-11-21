package org.truenewx.tnxjee.webmvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.ApplicationUtil;
import org.truenewx.tnxjee.core.version.VersionReader;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.util.SpringWebMvcUtil;

/**
 * war包控制器支持
 */
@RequestMapping("/war")
public abstract class WarControllerSupport {

    private static final String EXTENSION = ".war";

    @Autowired
    protected CommonProperties commonProperties;
    @Autowired
    protected VersionReader versionReader;

    @GetMapping("/size")
    @ConfigAnonymous
    @ResponseBody
    public Long getSize() {
        String dirLocation = ApplicationUtil.getWorkingDirLocation();
        File sourceFile = getSourceFile(dirLocation);
        if (sourceFile != null) {
            return sourceFile.length();
        }
        return null;
    }

    @GetMapping("/download")
    @ConfigAnonymous // 后台下载时不便携带Cookie，无法判断权限，故允许匿名下载
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dirLocation = ApplicationUtil.getWorkingDirLocation();
        File sourceFile = getSourceFile(dirLocation);
        if (sourceFile != null) {
            response.setContentLengthLong(sourceFile.length());
            String filename = getDownloadFilename(request) + EXTENSION;
            WebUtil.setDownloadFilename(request, response, filename);

            FileInputStream in = new FileInputStream(sourceFile);
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
            in.close();
        } else {
            response.getWriter().println("Source war file not found.");
        }
    }

    protected File getSourceFile(String workingDirLocation) {
        String tomcatRootLocation = ApplicationUtil.getTomcatRootLocation(workingDirLocation);
        if (tomcatRootLocation != null) { // 默认只支持在tomcat中运行的应用
            return new File(workingDirLocation + EXTENSION);
        } else if (ApplicationUtil.isInJar()) {
            return new File(workingDirLocation.substring(0, workingDirLocation.length() - 1));
        }
        return null;
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
        filename += Strings.MINUS + this.versionReader.getVersionText();
        return filename;
    }

}
