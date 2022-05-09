package org.truenewx.tnxjeex.fss.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.util.*;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.model.HttpHeaderRange;
import org.truenewx.tnxjee.web.util.WebUtil;
import org.truenewx.tnxjee.webmvc.bind.annotation.ResponseStream;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjeex.fss.api.FssContentReader;
import org.truenewx.tnxjeex.fss.api.FssManipulator;
import org.truenewx.tnxjeex.fss.api.FssMetaResolver;
import org.truenewx.tnxjeex.fss.api.model.FssTransferCommand;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.model.FssFileLocation;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;
import org.truenewx.tnxjeex.fss.model.FssUploadLimit;
import org.truenewx.tnxjeex.fss.service.FssExceptionCodes;
import org.truenewx.tnxjeex.fss.service.FssServiceTemplate;
import org.truenewx.tnxjeex.fss.web.model.FssUploadOptions;
import org.truenewx.tnxjeex.fss.web.model.FssUploadedFileMeta;

/**
 * 文件存储控制器模板
 *
 * @author jianglei
 */
public abstract class FssControllerTemplate<I extends UserIdentity<?>> implements FssMetaResolver, FssManipulator,
        FssContentReader {

    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String appName;
    @Autowired
    private CommonProperties commonProperties;
    @Autowired(required = false)
    protected FssServiceTemplate<I> service;
    @Autowired
    private Executor executor;
    /**
     * 下载地址前缀，应用可根据实际情况赋值
     */
    protected String downloadUrlPrefix;

    /**
     * 获取指定用户上传指定业务类型的文件上传配置
     *
     * @param type 业务类型
     * @return 指定用户上传指定业务类型的文件上传配置
     */
    @GetMapping("/upload-options/{type}")
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可读取上传配置
    public FssUploadOptions uploadOptions(@PathVariable("type") String type) {
        FssUploadLimit limit = this.service.getUploadLimit(type, getUserIdentity());
        boolean publicReadable = this.service.isPublicReadable(type);
        return new FssUploadOptions(limit, publicReadable);
    }

    @Override
    @ResponseBody
    @ConfigAuthority // 登录用户才可上传文件，服务策略可能还有更多限定
    public String upload(String type, String scope, MultipartFile file) {
        FssUploadedFileMeta meta = write(type, scope, file, null, true);
        return meta == null ? null : meta.getLocationUrl();
    }

    @PostMapping("/upload/{type}")
    @ResponseBody
    @ConfigAuthority // 登录用户才可上传文件，服务策略可能还有更多限定
    public FssUploadedFileMeta upload(@PathVariable("type") String type, MultipartHttpServletRequest request) {
        return upload(type, null, request);
    }

    // 指定业务范围上传
    @PostMapping("/upload/{type}/{scope}")
    @ResponseBody
    @ConfigAuthority // 登录用户才可上传文件，服务策略可能还有更多限定
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
                InputStream in = file.getInputStream();
                return write(type, scope, fileId, file.getSize(), file.getOriginalFilename(), in, onlyStorage);
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    private FssUploadedFileMeta write(String type, String scope, String fileId, long fileSize, String originalFilename,
            InputStream in, boolean onlyStorage) throws IOException {
        // 注意：此处获得的输入流大小与原始文件的大小可能不相同，可能变大或变小
        I userIdentity = getUserIdentity();
        String locationUrl = this.service.write(type, scope, userIdentity, fileSize, originalFilename, in);
        in.close();

        if (StringUtils.isBlank(fileId)) { // 如果文件id未指定，则根据存储路径加密得到文件id
            fileId = EncryptUtil.encryptByMd5(locationUrl);
        }

        FssUploadedFileMeta result = new FssUploadedFileMeta(fileId, locationUrl);
        if (!onlyStorage) { // 不只需要定位地址
            result.setName(originalFilename);
            String readUrl = this.service.getReadUrl(userIdentity, locationUrl, false);
            result.setReadUrl(getFullReadUrl(readUrl, true));
            result.setDownloadUrl(resolveDownloadUrl(locationUrl, false));
            FssUploadLimit uploadLimit = this.service.getUploadLimit(type, userIdentity);
            if (uploadLimit.isImageable()) {
                result.setImageable(true);
                result.setSize(ArrayUtil.get(uploadLimit.getSizes(), 0));
                // 缩略读取地址附加的缩略参数对最终URL可能产生影响，故需要重新生成，而不能在读取URL上简单附加缩略参数
                String thumbnailReadUrl = this.service.getReadUrl(userIdentity, locationUrl, true);
                result.setThumbnailReadUrl(getFullReadUrl(thumbnailReadUrl, true));
            }
        }
        return result;
    }

    @Override
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可获取，具体权限由服务策略决定
    public String resolveReadUrl(String locationUrl, boolean thumbnail) {
        if (StringUtils.isNotBlank(locationUrl)) {
            String readUrl = this.service.getReadUrl(getUserIdentity(), locationUrl, thumbnail);
            return getFullReadUrl(readUrl, true);
        }
        return null;
    }

    private String getFullReadUrl(String readUrl, boolean absolute) {
        // 读取地址以/开头但不以//开头，则视为相对地址，相对地址需考虑添加下载路径前缀、上下文根和主机地址
        if (readUrl != null && readUrl.startsWith(Strings.SLASH) && !readUrl.startsWith("//")) {
            // 加上下载路径前缀
            readUrl = getDownloadUrlPrefix() + readUrl;
            // 加上上下文根路径
            if (absolute) {
                readUrl = getContextUrl() + readUrl;
            }
        }
        return readUrl;
    }

    private String getContextUrl() {
        AppConfiguration app = this.commonProperties.getApp(this.appName);
        if (app != null) { // 有配置多应用的，从配置中获取上下文根路径
            return app.getContextUri(false);
        } else { // 否则取当前请求的上下文根路径
            HttpServletRequest request = Objects.requireNonNull(SpringWebContext.getRequest());
            String contextUrl = "//" + WebUtil.getHost(request, true);
            String contextPath = request.getContextPath();
            if (!contextPath.equals(Strings.SLASH)) {
                contextUrl += contextPath;
            }
            return contextUrl;
        }
    }

    @Override
    @ResponseBody
    @ConfigAnonymous
    public String resolveDownloadUrl(String locationUrl, boolean absolute) {
        if (locationUrl.startsWith(FssFileLocation.PROTOCOL)) {
            // 去掉协议部分，保留/开头
            String readUrl = locationUrl.substring(FssFileLocation.PROTOCOL.length() - 1);
            return getFullReadUrl(readUrl, absolute);
        }
        return null;
    }

    @Override
    @ResponseBody
    @ConfigAuthority // 登录用户才可转储资源，服务策略可能还有更多限定
    public String transfer(FssTransferCommand command) {
        String targetType = command.getTargetType();
        String sourceUrl = command.getSourceUrl();
        if (StringUtils.isNotBlank(targetType) && sourceUrl != null && NetUtil.isHttpUrl(sourceUrl, true)) {
            String contextUrl = WebUtil.getProtocolAndHost(SpringWebContext.getRequest()) + getDownloadUrlPrefix();
            // 不是本地读取地址，也不是第三方服务商的读取地址，才可以转换
            if (!sourceUrl.startsWith(contextUrl + Strings.SLASH) && !this.service.isOutsideReadUrl(targetType,
                    sourceUrl)) {
                try {
                    String sourceFilename = getFilename(sourceUrl, command.getExtension());
                    String fileId = StringUtil.uuid32();
                    File tempFile = new File(IOUtil.getTomcatTempDir(), fileId + Strings.UNDERLINE + sourceFilename);
                    NetUtil.download(sourceUrl, null, tempFile);
                    FssUploadedFileMeta meta = write(targetType, command.getTargetScope(), fileId, tempFile.length(),
                            sourceFilename, new FileInputStream(tempFile), true);
                    // 在独立线程中删除临时文件，以免影响正常流程
                    this.executor.execute(tempFile::delete);
                    return meta.getLocationUrl();
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            }
        }
        return sourceUrl;
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
     * 获取下载路径前缀<br>
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
    @ConfigAnonymous // 匿名用户即可获取，具体权限由服务策略决定
    public FssFileMeta resolveMeta(String locationUrl) {
        if (StringUtils.isNotBlank(locationUrl)) {
            FssFileMeta meta = this.service.getMeta(getUserIdentity(), locationUrl);
            if (meta != null) {
                meta.setReadUrl(getFullReadUrl(meta.getReadUrl(), true));
                meta.setThumbnailReadUrl(getFullReadUrl(meta.getThumbnailReadUrl(), true));
                meta.setDownloadUrl(resolveDownloadUrl(locationUrl, false));
            }
            return meta;
        }
        return null;
    }

    @Override
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可获取，具体权限由服务策略决定
    public FssFileMeta[] resolveMetas(String[] locationUrls) {
        FssFileMeta[] metas = new FssFileMeta[locationUrls.length];
        for (int i = 0; i < locationUrls.length; i++) {
            metas[i] = resolveMeta(locationUrls[i]);
        }
        return metas;
    }

    @GetMapping("/dl/**")
    @ResponseStream
    @ConfigAnonymous // 匿名用户即可读取，具体权限由服务策略决定
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        I userIdentity = getUserIdentity();
        String path = getDownloadPath(request);
        int bufferSize = IOUtil.DEFAULT_BUFFER_SIZE; // 必须与输出文件流时的缓存区大小保持一致
        FssFileDetail detail = resolveFileDetail(userIdentity, path, request, response, fssFileDetail -> {
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setBufferSize(bufferSize);
            if (Boolean.parseBoolean(request.getParameter("inline"))) { // 指定以内联方式下载
                response.setContentType(Mimetypes.getInstance().getMimetype(path));
            } else {
                WebUtil.setDownloadFilename(request, response, fssFileDetail.getOriginalFilename());
            }
        });
        if (detail != null) {
            long totalLength = detail.getLength();
            ServletOutputStream out = response.getOutputStream();
            List<HttpHeaderRange> ranges = WebUtil.getHeaderRanges(request);
            if (ranges.isEmpty()) { // 不分段
                this.service.read(userIdentity, path, out, 0, -1);
            } else if (ranges.size() == 1) { // 单个分段
                HttpHeaderRange range = ranges.get(0);
                long beginIndex = range.getBeginIndex(0L);
                long endIndex = range.getEndIndex(totalLength - 1); // 结束索引为包含关系，需要减1
                if (beginIndex > endIndex) {
                    response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + totalLength);
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return;
                }

                long contentLength;
                // 分段下载整个文件的请求，只输出一个缓存区的数据，以最快速度向客户端返回文件总长度
                if (beginIndex == 0 && range.getEndIndex() == null) {
                    contentLength = bufferSize;
                } else { // 分段下载非整个文件的请求，最大一次性输出64MB的数据
                    contentLength = Math.min(endIndex - beginIndex + 1, bufferSize * 1024 * 16);
                }
                endIndex = beginIndex + contentLength - 1;
                String contentRange = "bytes " + beginIndex + Strings.MINUS + endIndex + Strings.SLASH + totalLength;
                response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
                response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
                LogUtil.info(getClass(), "====== Range: {}, Content Range: {}, Content Length: {}",
                        range, contentRange, contentLength + " bytes");
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

                // 先设置头信息和状态，最后传输数据
                long time0 = System.currentTimeMillis();
                long actualLength = this.service.read(userIdentity, path, out, beginIndex, contentLength);
                LogUtil.info(getClass(), "====== Output: {} bytes and {}ms", actualLength,
                        System.currentTimeMillis() - time0);
            } else { // 多重分段
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 表示分段下载的状态码：206

                String rangeContentType = response.getContentType(); // 之前设置的响应ContentType作为分段ContentType
                String boundary = "MULTIPART_BOUNDARY_" + request.getSession().getId();
                response.setContentType("multipart/byteranges; boundary=" + boundary);
                for (HttpHeaderRange range : ranges) {
                    // 严格按照标准格式依次执行，不可更改代码顺序
                    out.println();
                    out.println("--" + boundary);
                    out.println(HttpHeaders.CONTENT_TYPE + ": " + rangeContentType);
                    String contentRange = "bytes " + range + Strings.SLASH + totalLength;
                    out.println(HttpHeaders.CONTENT_RANGE + ": " + contentRange);
                    long beginIndex = range.getBeginIndex(0L);
                    long time0 = System.currentTimeMillis();
                    long actualLength = this.service.read(userIdentity, path, out, beginIndex, range.getLength(-1));
                    out.println();
                    out.println("--" + boundary + "--");
                    LogUtil.info(getClass(), "====== Range: {}, Content Range: {}, Write: {}ms, Length: {}",
                            range, contentRange, (System.currentTimeMillis() - time0), actualLength + " bytes");
                }
            }
        }
    }

    protected String getDownloadPath(HttpServletRequest request) {
        String url = WebUtil.getRelativeRequestUrl(request);
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);
        String downloadUrlPrefix = getDownloadUrlPrefix();
        int index = url.indexOf(downloadUrlPrefix + Strings.SLASH);
        return url.substring(index + downloadUrlPrefix.length()); // 通配符部分
    }

    protected FssFileDetail resolveFileDetail(I userIdentity, String locationUrl,
            HttpServletRequest request, HttpServletResponse response, Consumer<FssFileDetail> prepare) {
        FssFileDetail detail = this.service.getDetail(userIdentity, locationUrl);
        if (detail == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            if (prepare != null) {
                prepare.accept(detail);
            }
            long modifiedTime = detail.getLastModifiedTime();
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, modifiedTime);
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            long modifiedSince = WebUtil.getDateHeader(request, HttpHeaders.IF_MODIFIED_SINCE);
            if (modifiedSince / 1000 == modifiedTime / 1000) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); // 如果相等则返回表示未修改的状态码：304
                return null;
            }
        }
        return detail;
    }

    @Override
    @ResponseBody
    @ConfigAnonymous // 匿名用户即可读取，具体权限由服务策略决定
    public String readText(String locationUrl, long limit) {
        I userIdentity = getUserIdentity();
        return this.service.readText(userIdentity, locationUrl, limit);
    }

    @Override
    @ResponseBody
    @ConfigAuthority // 登录用户才可删除文件，服务策略可能还有更多限定
    public void delete(String locationUrl) {
        I userIdentity = getUserIdentity();
        this.service.delete(userIdentity, locationUrl);
    }

    @Override
    @ResponseBody
    @ConfigAuthority // 登录用户才可删除文件，服务策略可能还有更多限定
    public void delete(String[] locationUrls) {
        if (locationUrls != null) {
            I userIdentity = getUserIdentity();
            for (String locationUrl : locationUrls) {
                this.service.delete(userIdentity, locationUrl);
            }
        }
    }

    protected I getUserIdentity() {
        return SecurityUtil.getAuthorizedUserIdentity();
    }

}
