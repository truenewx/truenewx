package org.truenewx.tnxjee.webmvc.qrcode;

import java.io.*;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.Mimetypes;
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
    private QrCodeGenerator qrCodeGenerator;

    @RequestMapping("/generate")
    @ConfigAnonymous
    @ResponseBody
    public String generate(@RequestParam("value") String value,
            @RequestParam(value = "size", required = false, defaultValue = "128") int size,
            @RequestParam(value = "logoUrl", required = false) String logoUrl) {
        try {
            return this.qrCodeGenerator.generate(value, size, logoUrl);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    @GetMapping("/{md5}")
    @ConfigAnonymous
    public String download(@PathVariable("md5") String md5,
            @RequestParam(value = "filename", required = false) String filename,
            HttpServletRequest request, HttpServletResponse response) {
        InputStream in = null;
        try {
            // 根据二维码图片的MD5码读取图片文件
            File imageFile = this.qrCodeGenerator.getImageFile(md5);
            if (imageFile.exists()) {
                if (StringUtils.isNotBlank(filename)) {
                    String extension = StringUtil.getExtension(imageFile.getName(), true);
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
                    OutputStream out = response.getOutputStream();
                    in = new FileInputStream(imageFile);
                    IOUtils.copy(in, out);
                    out.flush(); // 一定要强制刷新
                }
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            }
        }
        return null;
    }

    @GetMapping()
    @ConfigAnonymous
    public String view(@RequestParam("value") String value,
            @RequestParam(value = "size", required = false, defaultValue = "128") int size,
            @RequestParam(value = "logoUrl", required = false) String logoUrl,
            @RequestParam(value = "filename", required = false) String filename,
            HttpServletRequest request, HttpServletResponse response) {
        String md5 = generate(value, size, logoUrl);
        return download(md5, filename, request, response);
    }

}
