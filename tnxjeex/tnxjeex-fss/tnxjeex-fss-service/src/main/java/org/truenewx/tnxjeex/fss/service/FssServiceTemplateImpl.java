package org.truenewx.tnxjeex.fss.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.*;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;
import org.truenewx.tnxjeex.fss.model.FssUploadLimit;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;
import org.truenewx.tnxjeex.fss.service.model.FssStoragePath;
import org.truenewx.tnxjeex.fss.service.util.FssUtil;

/**
 * 文件存储服务模版实现
 *
 * @author jianglei
 */
public class FssServiceTemplateImpl<I extends UserIdentity<?>>
        implements FssServiceTemplate<I>, ContextInitializedBean {

    private final Map<String, FssAccessStrategy<I>> strategies = new HashMap<>();
    private final Map<FssProvider, FssAuthorizer> authorizers = new HashMap<>();
    private final Map<FssProvider, FssAccessor> accessors = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void afterInitialized(ApplicationContext context) {
        Map<String, FssAccessStrategy> strategies = context.getBeansOfType(FssAccessStrategy.class);
        for (FssAccessStrategy<I> strategy : strategies.values()) {
            this.strategies.put(strategy.getType(), strategy);
        }

        Map<String, FssAuthorizer> authorizers = context.getBeansOfType(FssAuthorizer.class);
        for (FssAuthorizer authorizer : authorizers.values()) {
            this.authorizers.put(authorizer.getProvider(), authorizer);
        }

        Map<String, FssAccessor> accessors = context.getBeansOfType(FssAccessor.class);
        for (FssAccessor accessor : accessors.values()) {
            this.accessors.put(accessor.getProvider(), accessor);
        }
    }

    @Override
    public FssUploadLimit getUploadLimit(String type, I userIdentity) {
        return getStrategy(type).getUploadLimit(userIdentity);
    }


    private FssAccessStrategy<I> getStrategy(String type) {
        FssAccessStrategy<I> strategy = this.strategies.get(type);
        if (strategy == null) {
            throw new BusinessException(FssExceptionCodes.NO_ACCESS_STRATEGY_FOR_TYPE, type);
        }
        return strategy;
    }

    @Override
    public boolean isPublicReadable(String type) {
        return getStrategy(type).isPublicReadable();
    }

    @Override
    public String write(String type, String scope, I userIdentity, long fileSize, String filename, InputStream in)
            throws IOException {
        FssAccessStrategy<I> strategy = getStrategy(type);
        // 上传限制校验
        FssUploadLimit uploadLimit = strategy.getUploadLimit(userIdentity);
        String extension = validateUploadLimit(uploadLimit, fileSize, filename);
        String relativeDir = getRelativeDirForWrite(strategy, userIdentity, scope);
        // 获取文件名
        String storageFilename = strategy.getStorageFilename(userIdentity, scope);
        if (StringUtils.isBlank(storageFilename)) {
            // 用BufferedInputStream装载以确保输入流可以标记和重置位置
            if (!(in instanceof BufferedInputStream)) {
                in = new BufferedInputStream(in);
            }
            in.mark(Integer.MAX_VALUE);
            storageFilename = EncryptUtil.encryptByMd5(in);
            in.reset();
        }
        storageFilename += extension.toLowerCase();
        // 构建存储路径
        FssStoragePath fsp = new FssStoragePath(type, relativeDir, storageFilename);
        String storagePath = NetUtil.standardizeUrl(strategy.getContextPath()) + fsp.getRelativePath();
        // 写文件
        FssProvider provider = strategy.getProvider();
        FssAccessor accessor = this.accessors.get(provider);
        accessor.write(in, storagePath, filename);
        // 写好文件之后，如果访问策略是公开匿名可读，则还需要进行相应授权，不过本地自有提供商无需进行授权
        if (strategy.isPublicReadable() && provider != FssProvider.OWN) {
            FssAuthorizer authorizer = this.authorizers.get(provider);
            authorizer.authorizePublicRead(storagePath);
        }
        String storageUrl = fsp.getUrl();
        strategy.onWritten(userIdentity, scope, storageUrl);
        return storageUrl;
    }

    /**
     * 校验上传限制
     *
     * @param fileSize 文件大小
     * @param filename 文件名
     * @return 包含.的扩展名
     */
    private String validateUploadLimit(FssUploadLimit uploadLimit, long fileSize, String filename) {
        if (fileSize > uploadLimit.getCapacity()) {
            throw new BusinessException(FssExceptionCodes.CAPACITY_EXCEEDS_LIMIT,
                    MathUtil.getCapacityCaption(uploadLimit.getCapacity(), 2));
        }
        String[] extensions = uploadLimit.getExtensions();
        String extension = FssUploadLimit.getExtension(filename);
        if (ArrayUtils.isNotEmpty(extensions)) { // 上传限制中没有设置扩展名，则不限定扩展名
            if (uploadLimit.isExtensionsRejected()) { // 拒绝扩展名模式
                if (ArrayUtil.containsIgnoreCase(extensions, extension)) {
                    throw new BusinessException(FssExceptionCodes.UNSUPPORTED_EXTENSION,
                            StringUtils.join(extensions, Strings.COMMA), filename);
                }
            } else { // 允许扩展名模式
                if (!ArrayUtil.containsIgnoreCase(extensions, extension)) {
                    throw new BusinessException(FssExceptionCodes.ONLY_SUPPORTED_EXTENSION,
                            StringUtils.join(extensions, Strings.COMMA), filename);
                }
            }
        }
        if (extension.length() > 0) {
            extension = Strings.DOT + extension;
        }
        return extension;
    }

    /**
     * 获取相对目录，同时校验写权限
     *
     * @param strategy     范围策略
     * @param userIdentity 用户标识
     * @param scope        业务范围
     * @return 相对目录
     */
    private String getRelativeDirForWrite(FssAccessStrategy<I> strategy, I userIdentity, String scope) {
        String relativeDir = strategy.getRelativeDir(userIdentity, scope);
        if (relativeDir == null) {
            throw new BusinessException(FssExceptionCodes.NO_WRITE_AUTHORITY);
        }
        return NetUtil.standardizeUrl(relativeDir);
    }

    @Override
    public String getReadUrl(I userIdentity, String storageUrl, boolean thumbnail) {
        FssStoragePath fsp = FssStoragePath.of(storageUrl);
        // fsp为null说明指定存储路径不满足内部存储路径格式
        return fsp == null ? storageUrl : getReadUrl(userIdentity, fsp, thumbnail);
    }

    private String getReadUrl(I userIdentity, FssStoragePath fsp, boolean thumbnail) {
        if (fsp != null && fsp.isValid()) {
            FssAccessStrategy<I> strategy = validateUserRead(userIdentity, fsp);
            FssProvider provider = strategy.getProvider();
            if (provider == FssProvider.OWN) {
                // 本地自有提供商的读取URL与存储URL保持一致，以便于读取时判断所属访问策略
                return fsp.toString();
            } else {
                FssAuthorizer authorizer = this.authorizers.get(provider);
                String path = NetUtil.standardizeUrl(strategy.getContextPath()) + fsp.getRelativePath();
                if (thumbnail) {
                    path = appendThumbnailParameters(strategy, path);
                }
                return authorizer.getReadUrl(userIdentity, path);
            }
        }
        return null;
    }

    private String appendThumbnailParameters(FssAccessStrategy<I> strategy, String path) {
        if (strategy != null) {
            Map<String, String> thumbnailParameters = strategy.getThumbnailParameters();
            if (thumbnailParameters != null && thumbnailParameters.size() > 0) {
                StringBuilder params = new StringBuilder();
                for (Entry<String, String> entry : thumbnailParameters.entrySet()) {
                    params.append(Strings.AND).append(entry.getKey()).append(Strings.EQUAL).append(entry.getValue());
                }
                if (params.length() > 0) {
                    params.deleteCharAt(0);
                }
                int index = path.indexOf(Strings.QUESTION);
                // 确保缩略参数作为优先参数
                if (index > 0) {
                    path = path.substring(0, index + 1) + params + Strings.AND + path.substring(index + 1);
                } else {
                    path += Strings.QUESTION + params;
                }
            }
        }
        return path;
    }

    @Override
    public boolean isOutsideReadUrl(String type, String url) {
        FssAccessStrategy<I> strategy = getStrategy(type);
        FssProvider provider = strategy.getProvider();
        if (provider == FssProvider.OWN) {
            return false;
        }
        // 外部读取地址一定是http地址
        if (NetUtil.isHttpUrl(url, true)) {
            FssAuthorizer authorizer = this.authorizers.get(provider);
            if (authorizer != null) {
                String contextUrl = authorizer.getContextUrl();
                String contextPath = NetUtil.standardizeUrl(strategy.getContextPath());
                // 当contextPath对应多个type时，可能将其它业务类型的地址判断为正确，但一定不会将正确的业务类型判断为错误
                return url.startsWith(contextUrl + contextPath + Strings.SLASH);
            }
        }
        return false;
    }

    private FssAccessStrategy<I> validateUserRead(I userIdentity, FssStoragePath fsp) {
        if (fsp.isValid()) {
            FssAccessStrategy<I> strategy = getStrategy(fsp.getType());
            if (strategy.isReadable(userIdentity, fsp.getRelativeDir(), fsp.getFilename())) {
                return strategy;
            }
        }
        throw new BusinessException(FssExceptionCodes.NO_READ_AUTHORITY, fsp.getUrl());
    }

    @Override
    public FssFileMeta getMeta(I userIdentity, String storageUrl) {
        if (StringUtils.isNotBlank(storageUrl)) {
            FssStoragePath fsp = FssStoragePath.of(storageUrl);
            if (fsp != null) {
                FssAccessStrategy<I> strategy = validateUserRead(userIdentity, fsp);
                FssAccessor accessor = this.accessors.get(strategy.getProvider());
                String path = NetUtil.standardizeUrl(strategy.getContextPath()) + fsp.getRelativePath();
                try {
                    FssFileDetail detail = accessor.getDetail(path);
                    if (detail != null) {
                        FssFileMeta meta = new FssFileMeta(storageUrl);
                        meta.setName(detail.getFilename());
                        meta.setReadUrl(getReadUrl(userIdentity, fsp, false));
                        meta.setThumbnailReadUrl(getReadUrl(userIdentity, fsp, true));
                        FssUploadLimit uploadLimit = strategy.getUploadLimit(userIdentity);
                        if (uploadLimit.isImageable()) {
                            meta.setImageable(true);
                            meta.setSize(ArrayUtil.get(uploadLimit.getSizes(), 0));
                        }
                        return meta;
                    }
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            }
        }
        return null;
    }

    @Override
    public FssFileDetail getDetail(I userIdentity, String storageUrl) {
        FssStoragePath fsp = FssStoragePath.of(storageUrl);
        if (fsp != null) {
            FssAccessStrategy<I> strategy = validateUserRead(userIdentity, fsp);
            FssAccessor accessor = this.accessors.get(strategy.getProvider());
            String path = NetUtil.standardizeUrl(strategy.getContextPath()) + fsp.getRelativePath();
            try {
                FssFileDetail detail = accessor.getDetail(path);
                String downloadFilename = strategy.getDownloadFilename(userIdentity, fsp.getRelativeDir(),
                        fsp.getFilename());
                if (StringUtils.isNotBlank(downloadFilename)) {
                    detail = new FssFileDetail(downloadFilename, detail.getLastModifiedTime(), detail.getLength());
                }
                return detail;
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

    @Override
    public InputStream getReadStream(I userIdentity, String storageUrl) {
        Binate<String, FssAccessor> binate = getPathAccessor(userIdentity, storageUrl);
        if (binate != null) {
            String path = binate.getLeft();
            FssAccessor accessor = binate.getRight();
            try {
                return accessor.getReadStream(path);
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

    private Binate<String, FssAccessor> getPathAccessor(I userIdentity, String storageUrl) {
        FssStoragePath fsp = FssStoragePath.of(storageUrl);
        if (fsp != null) {
            FssAccessStrategy<I> strategy = validateUserRead(userIdentity, fsp);
            String path = NetUtil.standardizeUrl(strategy.getContextPath()) + fsp.getRelativePath();
            FssAccessor accessor = this.accessors.get(strategy.getProvider());
            return new Binary<>(path, accessor);
        }
        return null;
    }

    @Override
    public long read(I userIdentity, String storageUrl, OutputStream out, long offset, long expectedLength) {
        InputStream in = getReadStream(userIdentity, storageUrl);
        if (in != null) {
            long actualLength = IOUtil.copyAsPossible(in, out, offset, expectedLength);
            try {
                in.close();
            } catch (IOException e) { // 处理此处的IOException，已确保返回读取长度
                LogUtil.error(getClass(), e);
            }
            return actualLength;
        }
        return 0;
    }

    @Override
    public String readText(I userIdentity, String storageUrl, long limit) {
        Binate<String, FssAccessor> binate = getPathAccessor(userIdentity, storageUrl);
        if (binate != null) {
            String path = binate.getLeft();
            FssAccessor accessor = binate.getRight();
            try {
                InputStream in = accessor.getReadStream(path);
                if (in != null) {
                    // 未指定读取限制，或文件大小未超过限制，才读取内容
                    if (limit <= 0 || in.available() <= limit) {
                        Charset charset = accessor.getCharset(path);
                        if (charset == null) {
                            charset = FssUtil.getCharset(in);
                        }
                        if (charset == null) {
                            throw new BusinessException(FssExceptionCodes.IS_NOT_TEXT_FILE, storageUrl);
                        }
                        String encoding = charset.toString();
                        String content = IOUtils.toString(in, encoding);
                        in.close();
                        return content;
                    }
                }
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

    @Override
    public void delete(I userIdentity, String storageUrl) {
        FssStoragePath fsp = FssStoragePath.of(storageUrl);
        if (fsp != null) {
            FssAccessStrategy<I> strategy = getStrategy(fsp.getType());
            String relativeDir = fsp.getRelativeDir();
            if (!strategy.isDeletable(userIdentity, relativeDir, fsp.getFilename())) {
                throw new BusinessException(FssExceptionCodes.NO_DELETE_AUTHORITY, fsp.getUrl());
            }
            FssAccessor accessor = this.accessors.get(strategy.getProvider());
            String contextPath = NetUtil.standardizeUrl(strategy.getContextPath());
            String path = contextPath + fsp.getRelativePath();
            accessor.delete(path, strategy);
        }
    }

    @Override
    public String copy(I userIdentity, String sourceStorageUrl, String targetType, String targetScope) {
        FssStoragePath sourceStoragePath = FssStoragePath.of(sourceStorageUrl);
        if (sourceStoragePath != null) {
            FssAccessStrategy<I> sourceStrategy = getStrategy(sourceStoragePath.getType());
            FssAccessStrategy<I> targetStrategy = getStrategy(targetType);
            // 跨文件存储服务提供商无法复制（暂时没有跨文件存储服务提供商的需要，实际上也是可行的，只是性能较差，代码复杂）
            if (sourceStrategy.getProvider() != targetStrategy.getProvider()) {
                throw new BusinessException(FssExceptionCodes.CANNOT_COPY_BETWEEN_PROVIDERS);
            }
            // 获取目标相对目录，同时校验写权限
            String targetRelativeDir = getRelativeDirForWrite(targetStrategy, userIdentity, targetScope);
            // 获取目标存储文件名
            String targetStorageFilename = targetStrategy.getStorageFilename(userIdentity, targetScope);
            if (StringUtils.isBlank(targetStorageFilename)) {
                throw new BusinessException(FssExceptionCodes.CANNOT_COPY_WITHOUT_STORAGE_FILENAME_BY_SCOPE,
                        targetScope, targetType);
            }
            // 目标文件扩展名与来源文件相同
            String extension = FilenameUtils.getExtension(sourceStoragePath.getFilename());
            if (StringUtils.isNotBlank(extension) && !extension.startsWith(Strings.DOT)) {
                extension = Strings.DOT + extension;
            }
            targetStorageFilename += extension.toLowerCase();
            // 构建目标存储路径
            FssStoragePath targetStoragePath = new FssStoragePath(targetType, targetRelativeDir, targetStorageFilename);
            String targetStorageUrl = targetStoragePath.getUrl();
            if (!sourceStorageUrl.equals(targetStorageUrl)) { // 存储路径不同才有必要复制
                FssAccessor accessor = this.accessors.get(targetStrategy.getProvider());
                accessor.copy(sourceStoragePath.toString(), targetStoragePath.toString());
                targetStrategy.onWritten(userIdentity, targetScope, targetStorageUrl);
            }
            return targetStorageUrl;
        }
        return null;
    }

}
