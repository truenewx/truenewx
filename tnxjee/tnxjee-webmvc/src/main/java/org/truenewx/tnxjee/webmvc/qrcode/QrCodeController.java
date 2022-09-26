package org.truenewx.tnxjee.webmvc.qrcode;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.Mimetypes;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;

/**
 * 二维码控制器
 *
 * @author liuzhiyi
 */
@Controller
@RequestMapping("/qrcode")
public class QrCodeController {

    @Autowired
    private QrCodeGenerator generator;

    @GetMapping()
    @ConfigAnonymous
    public String generate(@RequestParam("value") String value,
            @RequestParam(value = "size", required = false, defaultValue = "128") int size,
            @RequestParam(value = "logoUrl", required = false) String logoUrl,
            @RequestParam(value = "margin", required = false, defaultValue = "0") int margin,
            @RequestParam(value = "filename", required = false) String filename,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        value = URLDecoder.decode(value, StandardCharsets.UTF_8);
        if (logoUrl != null) {
            logoUrl = URLDecoder.decode(logoUrl, StandardCharsets.UTF_8);
            if (NetUtil.isRelativeUrl(logoUrl)) { // 相对地址需加上当前站点上下文地址
                logoUrl = WebUtil.getContextUrl(request) + logoUrl;
            }
        }
        if (StringUtils.isNotBlank(filename)) { // 指定了下载文件名，表示有下载二维码图片的可能性，说明为变化可能性低的静态图片，生成缓存文件以提高访问性能
            String name = this.generator.save(value, size, logoUrl, margin);
            filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
            return download(name, filename, request, response);
        }
        try (InputStream in = this.generator.getInputStream(value, size, logoUrl, margin)) {
            String contentType = Mimetypes.getInstance().getMimetype(QrCodeGenerator.EXTENSION);
            response.setContentType(contentType);
            write(response, in);
        }
        return null;
    }

    private void write(HttpServletResponse response, InputStream in) throws IOException {
        OutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
        out.flush(); // 一定要强制刷新
        in.close();
    }

    @GetMapping("/{name}")
    @ConfigAnonymous
    public String download(@PathVariable("name") String name,
            @RequestParam(value = "filename", required = false) String filename,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 根据二维码图片的MD5码读取图片文件
        File imageFile = this.generator.getImageFileByName(name);
        if (imageFile.exists()) {
            if (StringUtils.isNotBlank(filename)) {
                String extension = StringUtil.getExtension(imageFile.getName(), true);
                filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
                WebUtil.setDownloadFilename(request, response, filename + extension);
            } else {
                String contentType = Mimetypes.getInstance().getMimetype(imageFile.getName());
                response.setContentType(contentType);
            }

            // 性能优化处理，如果文件已存在并且未发生过改变则直接返回304状态码
            Date lastModifiedTime = new Date(request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE));
            Date imageLastModifiedTime = new Date(imageFile.lastModified());
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, imageLastModifiedTime.getTime());
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            if (DateUtil.secondsBetween(imageLastModifiedTime, lastModifiedTime) == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 如果相等则返回304状态码
            } else {
                try (InputStream in = new FileInputStream(imageFile)) {
                    write(response, in);
                }
            }
        }
        return null;
    }

}
