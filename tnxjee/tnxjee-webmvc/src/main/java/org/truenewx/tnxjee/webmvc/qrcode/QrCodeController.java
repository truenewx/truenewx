package org.truenewx.tnxjee.webmvc.qrcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 二维码控制器
 *
 * @author liuzhiyi
 */
@Controller
public class QrCodeController {

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @GetMapping("/qrcode/{md5}")
    public String execute(@PathVariable("md5") String md5, HttpServletRequest request,
            HttpServletResponse response) {
        InputStream in = null;
        try {
            // 读取二维码文件
            File imageFile = this.qrCodeGenerator.getImageFile(md5);
            if (imageFile.exists()) {
                // 性能优化处理,如果文件已存在并且未发生过改变则直接返回304状态码
                Date lastModifiedTime = new Date(request.getDateHeader("If-Modified-Since"));
                Date imageLastModifiedTime = new Date(imageFile.lastModified());
                response.setContentType("image/*");
                response.setDateHeader("Last-Modified", imageLastModifiedTime.getTime());
                OutputStream out = response.getOutputStream();
                if (DateUtil.secondsBetween(imageLastModifiedTime, lastModifiedTime) != 0) {
                    in = new FileInputStream(imageFile);
                    IOUtils.copy(in, out);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 如果相等则返回304状态码
                }
                out.flush(); // 一定要关闭
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
}
