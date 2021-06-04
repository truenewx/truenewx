package org.truenewx.tnxjeex.fss.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.spec.upload.FileUploadLimit;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.bind.annotation.ResponseStream;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjeex.fss.api.FssMetaResolver;
import org.truenewx.tnxjeex.fss.api.FssUploader;
import org.truenewx.tnxjeex.fss.api.model.FssTransferCommand;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;
import org.truenewx.tnxjeex.fss.service.FssExceptionCodes;
import org.truenewx.tnxjeex.fss.service.FssServiceTemplate;
import org.truenewx.tnxjeex.fss.web.model.FssUploadedFileMeta;

import com.aliyun.oss.internal.Mimetypes;

/**
 * 文件存储控制器模板
 *
 * @author jianglei
 */
public abstract class FssControllerTemplate<I extends UserIdentity<?>> implements FssMetaResolver, FssUploader {

    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String appName;
    @Autowired
    private CommonProperties commonProperties;
    @Autowired(required = false)
    private FssServiceTemplate<I> service;
    @Autowired
    private Executor executor;

    protected String downloadUrlPrefix;

    /**
     * 获取指定用户上传指定业务类型的文件上传限制条件
     *
     * @param type 业务类型
     * @return 指定用户上传指定业务类型的文件上传限制条件
     */
    @GetMapping("/upload-limit/{type}")
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可读取上传限制
    public FileUploadLimit getUploadLimit(@PathVariable("type") String type) {
        return this.service.getUploadLimit(type, getUserIdentity());
    }

    @Override
    @ResponseBody
    @ConfigAuthority // 登录用户才可上传文件，访问策略可能还有更多限定
    public String upload(String type, String scope, MultipartFile file) {
        FssUploadedFileMeta meta = write(type, scope, file, null, true);
        return meta == null ? null : meta.getStorageUrl();
    }

    @PostMapping("/upload/{type}")
    @ResponseBody
    @ConfigAuthority // 登录用户才可上传文件，访问策略可能还有更多限定
    public FssUploadedFileMeta upload(@PathVariable("type") String type, MultipartHttpServletRequest request) {
        return upload(type, null, request);
    }

    // 指定业务范围上传
    @PostMapping("/upload/{type}/{scope}")
    @ResponseBody
    @ConfigAuthority // 登录用户才可上传文件，访问策略可能还有更多限定
    public FssUploadedFileMeta upload(@PathVariable("type") String type, @PathVariable("scope") String scope,
            MultipartHttpServletRequest request) {
        boolean onlyStorage = Boolean.parseBoolean(request.getParameter("onlyStorage"));
        String fileId = request.getParameter("fileId");
        MultipartFile file = WebUtil.getMultipartFile(request, "file");
        return write(type, scope, file, fileId, onlyStorage);
    }

    private FssUploadedFileMeta write(String type, String scope, MultipartFile file, String fileId,
            boolean onlyStorage) {
        try {
            if (file != null) {
                String filename = file.getOriginalFilename();
                InputStream in = file.getInputStream();
                return write(type, scope, fileId, file.getSize(), filename, in, onlyStorage);
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    private FssUploadedFileMeta write(String type, String scope, String fileId, long fileSize, String filename,
            InputStream in, boolean onlyStorage) throws IOException {
        // 注意：此处获得的输入流大小与原始文件的大小可能不相同，可能变大或变小
        I userIdentity = getUserIdentity();
        String storageUrl = this.service.write(type, scope, userIdentity, fileSize, filename, in);
        in.close();

        if (StringUtils.isBlank(fileId)) { // 如果文件id未指定，则根据存储路径加密得到文件id
            fileId = EncryptUtil.encryptByMd5(storageUrl);
        }

        FssUploadedFileMeta result;
        if (onlyStorage) { // 只需要存储地址
            result = new FssUploadedFileMeta(fileId, null, storageUrl, null, null);
        } else {
            String readUrl = this.service.getReadUrl(userIdentity, storageUrl, false);
            readUrl = getFullReadUrl(readUrl);
            // 缩略读取地址附加的缩略参数对最终URL可能产生影响，故需要重新生成，而不能在读取URL上简单附加缩略参数
            String thumbnailReadUrl = this.service.getReadUrl(userIdentity, storageUrl, true);
            thumbnailReadUrl = getFullReadUrl(thumbnailReadUrl);
            result = new FssUploadedFileMeta(fileId, filename, storageUrl, readUrl, thumbnailReadUrl);
        }
        return result;
    }

    @Override
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可获取，具体权限由访问策略决定
    public String resolveReadUrl(String storageUrl, boolean thumbnail) {
        if (StringUtils.isNotBlank(storageUrl)) {
            String readUrl = this.service.getReadUrl(getUserIdentity(), storageUrl, thumbnail);
            return getFullReadUrl(readUrl);
        }
        return null;
    }

    private String getFullReadUrl(String readUrl) {
        // 读取地址以/开头但不以//开头，则视为相对地址，相对地址需考虑添加下载路径前缀、上下文根和主机地址
        if (readUrl != null && readUrl.startsWith(Strings.SLASH) && !readUrl.startsWith("//")) {
            // 加上下载路径前缀
            readUrl = getDownloadUrlPrefix() + readUrl;
            // 加上上下文根路径
            readUrl = getContextUri() + readUrl;
        }
        return readUrl;
    }

    private String getContextUri() {
        AppConfiguration app = this.commonProperties.getApp(this.appName);
        if (app != null) { // 有配置多应用的，从配置中获取上下文根路径
            return app.getContextUri(false);
        } else { // 否则取当前请求的上下文根路径
            HttpServletRequest request = Objects.requireNonNull(SpringWebContext.getRequest());
            String contextUri = "//" + WebUtil.getHost(request, true);
            String contextPath = request.getContextPath();
            if (!contextPath.equals(Strings.SLASH)) {
                contextUri += contextPath;
            }
            return contextUri;
        }
    }

    @Override
    @ResponseBody
    @ConfigAuthority // 登录用户才可转储资源，访问策略可能还有更多限定
    public String transfer(FssTransferCommand command) {
        String type = command.getType();
        String url = command.getUrl();
        if (StringUtils.isNotBlank(type) && url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            try {
                String filename = getFilename(url, command.getExtension());
                File root = new ClassPathResource(".").getFile().getParentFile().getParentFile().getParentFile()
                        .getParentFile();
                String fileId = StringUtil.uuid32();
                File file = new File(root, "/temp/" + fileId + Strings.UNDERLINE + filename);
                NetUtil.download(url, null, file);
                FssUploadedFileMeta meta = write(type, command.getScope(), fileId, file.length(), filename,
                        new FileInputStream(file), true);
                // 在独立线程中删除临时文件，以免影响正常流程
                this.executor.execute(file::delete);
                return meta.getStorageUrl();
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return command.getUrl(); // url变量可能已被改变，此处需返回原始URL
    }

    private String getFilename(String url, String extension) {
        String filename = url;
        int index = url.lastIndexOf(Strings.SLASH);
        if (index >= 0) {
            filename = filename.substring(index + 1);
        }
        if (!filename.contains(Strings.DOT)) { // 从url中取得的文件名中不包含扩展名，则加上扩展名参数
            if (StringUtils.isBlank(extension)) { // 此时扩展名不能为空
                throw new BusinessException(FssExceptionCodes.NO_EXTENSION, url);
            }
            if (!extension.startsWith(Strings.DOT)) {
                filename += Strings.DOT;
            }
            filename += extension;
        }
        return filename;
    }

    /**
     * 获取下载路径前缀<br/>
     * 子类如果覆写，必须与download()方法的路径前缀相同
     *
     * @return 下载路径前缀
     */
    protected String getDownloadUrlPrefix() {
        if (this.downloadUrlPrefix == null) {
            String prefix = Strings.EMPTY;
            RequestMapping requestMapping = getClass().getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] paths = requestMapping.value();
                if (paths.length == 1) {
                    prefix = paths[0];
                }
            }
            this.downloadUrlPrefix = prefix + "/dl";
        }
        return this.downloadUrlPrefix;
    }

    @Override
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可获取，具体权限由访问策略决定
    public FssFileMeta resolveMeta(String storageUrl) {
        if (StringUtils.isNotBlank(storageUrl)) {
            FssFileMeta meta = this.service.getMeta(getUserIdentity(), storageUrl);
            if (meta != null) {
                meta.setReadUrl(getFullReadUrl(meta.getReadUrl()));
                meta.setThumbnailReadUrl(getFullReadUrl(meta.getThumbnailReadUrl()));
            }
            return meta;
        }
        return null;
    }

    @Override
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可获取，具体权限由访问策略决定
    public FssFileMeta[] resolveMetas(String[] storageUrls) {
        FssFileMeta[] metas = new FssFileMeta[storageUrls.length];
        for (int i = 0; i < storageUrls.length; i++) {
            metas[i] = resolveMeta(storageUrls[i]);
        }
        return metas;
    }

    @GetMapping("/dl/**")
    @ResponseStream
    @ConfigAnonymous // 匿名用户即可读取，具体权限由访问策略决定
    public String download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        I userIdentity = getUserIdentity();
        String path = getDownloadPath(request);
        long modifiedTime = this.service.getLastModifiedTime(userIdentity, path);
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, modifiedTime);
        response.setContentType(Mimetypes.getInstance().getMimetype(path));
        long modifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (modifiedSince == modifiedTime) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 如果相等则返回表示未修改的状态码
        } else {
            ServletOutputStream out = response.getOutputStream();
            this.service.read(userIdentity, path, out);
            out.close();
        }
        return null;
    }

    protected String getDownloadPath(HttpServletRequest request) {
        String url = WebUtil.getRelativeRequestUrl(request);
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);
        String downloadUrlPrefix = getDownloadUrlPrefix();
        int index = url.indexOf(downloadUrlPrefix + Strings.SLASH);
        return url.substring(index + downloadUrlPrefix.length()); // 通配符部分
    }

    protected I getUserIdentity() {
        return SecurityUtil.getAuthorizedUserIdentity();
    }

}
