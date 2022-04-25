package org.truenewx.tnxjeex.fss.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import org.truenewx.tnxjeex.fss.model.FssFileLocation;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;
import org.truenewx.tnxjeex.fss.model.FssUploadLimit;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageAccessor;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageAuthorizer;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageProvider;
import org.truenewx.tnxjeex.fss.service.util.FssUtil;

/**
 * 文件存储服务模版实现
 *
 * @author jianglei
 */
public class FssServiceTemplateImpl<I extends UserIdentity<?>>
        implements FssServiceTemplate<I>, ContextInitializedBean {

    private final Map<String, FssServiceStrategy<I>> strategies = new HashMap<>();
    private final Map<FssStorageProvider, FssStorageAuthorizer> authorizers = new HashMap<>();
    private final Map<FssStorageProvider, FssStorageAccessor> accessors = new HashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void afterInitialized(ApplicationContext context) {
        Map<String, FssServiceStrategy> strategies = context.getBeansOfType(FssServiceStrategy.class);
        for (FssServiceStrategy<I> strategy : strategies.values()) {
            this.strategies.put(strategy.getType(), strategy);
        }

        Map<String, FssStorageAuthorizer> authorizers = context.getBeansOfType(FssStorageAuthorizer.class);
        for (FssStorageAuthorizer authorizer : authorizers.values()) {
            this.authorizers.put(authorizer.getProvider(), authorizer);
        }

        Map<String, FssStorageAccessor> accessors = context.getBeansOfType(FssStorageAccessor.class);
        for (FssStorageAccessor accessor : accessors.values()) {
            this.accessors.put(accessor.getProvider(), accessor);
        }
    }

    @Override
    public FssUploadLimit getUploadLimit(String type, I userIdentity) {
        return getStrategy(type).getUploadLimit(userIdentity);
    }


    private FssServiceStrategy<I> getStrategy(String type) {
        FssServiceStrategy<I> strategy = this.strategies.get(type);
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
    public String write(String type, String scope, I userIdentity, long fileSize, String originalFilename,
            InputStream in) throws IOException {
        FssServiceStrategy<I> strategy = getStrategy(type);
        // 上传限制校验
        FssUploadLimit uploadLimit = strategy.getUploadLimit(userIdentity);
        String extension = validateUploadLimit(uploadLimit, fileSize, originalFilename);
        String storageDir = getStorageDirForWrite(strategy, userIdentity, scope);
        // 获取存储文件名
        String storageFilename = strategy.getStorageFilename(userIdentity, scope, originalFilename);
        if (storageFilename.contains(FssServiceStrategy.PLACEHOLDER_MD5)) {
            // 用BufferedInputStream装载以确保输入流可以标记和重置位置
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            in.mark(Integer.MAX_VALUE);
            storageFilename = storageFilename.replace(FssServiceStrategy.PLACEHOLDER_MD5, EncryptUtil.encryptByMd5(in));
            in.reset();
        }
        storageFilename += extension.toLowerCase();
        // 构建存储路径
        String storagePath = getStoragePath(storageDir, storageFilename);
        // 写文件
        FssStorageProvider provider = strategy.getProvider();
        FssStorageAccessor accessor = this.accessors.get(provider);
        accessor.write(in, storagePath, originalFilename);
        // 写好文件之后，如果服务策略是公开匿名可读，则还需要进行相应授权，不过本地自有提供商无需进行授权
        if (strategy.isPublicReadable() && provider != FssStorageProvider.OWN) {
            FssStorageAuthorizer authorizer = this.authorizers.get(provider);
            authorizer.authorizePublicRead(storagePath);
        }
        // 构建定位地址
        String locationPath = strategy.getLocationPath(storagePath);
        strategy.onWritten(userIdentity, locationPath);
        return FssFileLocation.toUrl(type, locationPath);
    }

    private String getStoragePath(String storageDir, String storageFilename) {
        String storagePath = storageDir + Strings.SLASH + storageFilename;
        return cleanIllegalChars(storagePath);
    }

    // 清理非法字符，以免作为路径的一部分无法正常加载
    private String cleanIllegalChars(String path) {
        return path.replaceAll("[+%]", Strings.SPACE); // 因长度为关键的判断依据，故需保证长度不变
    }

    /**
     * 校验上传限制
     *
     * @param fileSize         文件大小
     * @param originalFilename 原始文件名
     * @return 包含.的扩展名
     */
    private String validateUploadLimit(FssUploadLimit uploadLimit, long fileSize, String originalFilename) {
        if (fileSize > uploadLimit.getCapacity()) {
            throw new BusinessException(FssExceptionCodes.CAPACITY_EXCEEDS_LIMIT,
                    MathUtil.getCapacityCaption(uploadLimit.getCapacity(), 2));
        }
        String[] extensions = uploadLimit.getExtensions();
        String extension = StringUtil.getExtension(originalFilename);
        if (ArrayUtils.isNotEmpty(extensions)) { // 上传限制中没有设置扩展名，则不限定扩展名
            if (uploadLimit.isExtensionsRejected()) { // 拒绝扩展名模式
                if (ArrayUtil.containsIgnoreCase(extensions, extension)) {
                    throw new BusinessException(FssExceptionCodes.UNSUPPORTED_EXTENSION,
                            StringUtils.join(extensions, Strings.COMMA), originalFilename);
                }
            } else { // 允许扩展名模式
                if (!ArrayUtil.containsIgnoreCase(extensions, extension)) {
                    throw new BusinessException(FssExceptionCodes.ONLY_SUPPORTED_EXTENSION,
                            StringUtils.join(extensions, Strings.COMMA), originalFilename);
                }
            }
        }
        if (extension.length() > 0) {
            extension = Strings.DOT + extension;
        }
        return extension;
    }

    /**
     * 获取存储相对目录，同时校验写权限
     *
     * @param strategy     范围策略
     * @param userIdentity 用户标识
     * @param scope        业务范围
     * @return 存储相对目录
     */
    private String getStorageDirForWrite(FssServiceStrategy<I> strategy, I userIdentity, String scope) {
        String storageDir = strategy.getStorageRelativeDir(userIdentity, scope);
        if (storageDir == null) {
            throw new BusinessException(FssExceptionCodes.NO_WRITE_AUTHORITY);
        }
        return strategy.getStorageRootDir() + NetUtil.standardizeUrl(storageDir);
    }

    @Override
    public void write(String locationUrl, I userIdentity, long fileSize, String originalFilename, InputStream in)
            throws IOException {
        FssFileLocation location = FssFileLocation.of(locationUrl);
        if (location != null) {
            FssServiceStrategy<I> strategy = getStrategy(location.getType());
            // 上传限制校验
            FssUploadLimit uploadLimit = strategy.getUploadLimit(userIdentity);
            validateUploadLimit(uploadLimit, fileSize, originalFilename);

            if (!strategy.isWriteable(userIdentity, location.getDir(), location.getFilename())) {
                throw new BusinessException(FssExceptionCodes.NO_WRITE_AUTHORITY);
            }

            FssStorageProvider provider = strategy.getProvider();
            FssStorageAccessor accessor = this.accessors.get(provider);
            String storagePath = getStoragePath(strategy, location);
            accessor.write(in, storagePath, originalFilename);
        }
    }

    private String getStoragePath(FssServiceStrategy<I> strategy, FssFileLocation location) {
        String storagePath = strategy.getStoragePath(location.getPath());
        // 转换特殊字符，以免作为路径的一部分无法正常加载
        return cleanIllegalChars(storagePath);
    }

    @Override
    public String getReadUrl(I userIdentity, String locationUrl, boolean thumbnail) {
        FssFileLocation location = FssFileLocation.of(locationUrl);
        // location为null说明指定定位地址不满足定位地址的格式规范
        return location == null ? locationUrl : getReadUrl(userIdentity, location, thumbnail);
    }

    private String getReadUrl(I userIdentity, FssFileLocation location, boolean thumbnail) {
        if (location != null) {
            FssServiceStrategy<I> strategy = validateUserRead(userIdentity, location);
            FssStorageProvider provider = strategy.getProvider();
            if (provider == FssStorageProvider.OWN) {
                // 本地自有提供商的读取URL与定位地址保持一致，以便于读取时判断所属服务策略
                return location.toString();
            } else {
                FssStorageAuthorizer authorizer = this.authorizers.get(provider);
                String storagePath = getStoragePath(strategy, location);
                if (thumbnail) {
                    storagePath = appendThumbnailParameters(strategy, storagePath);
                }
                return authorizer.getReadUrl(userIdentity, storagePath);
            }
        }
        return null;
    }

    private String appendThumbnailParameters(FssServiceStrategy<I> strategy, String path) {
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
        FssServiceStrategy<I> strategy = getStrategy(type);
        FssStorageProvider provider = strategy.getProvider();
        if (provider == FssStorageProvider.OWN) {
            return false;
        }
        // 外部读取地址一定是http(s)地址
        if (NetUtil.isHttpUrl(url, true)) {
            FssStorageAuthorizer authorizer = this.authorizers.get(provider);
            if (authorizer != null) {
                String contextUrl = authorizer.getReadContextUrl();
                // 外部读取地址去掉上下文根后，应以业务服务策略的存储根目录开头
                return url.startsWith(contextUrl + strategy.getStorageRootDir() + Strings.SLASH);
            }
        }
        return false;
    }

    private FssServiceStrategy<I> validateUserRead(I userIdentity, FssFileLocation location) {
        FssServiceStrategy<I> strategy = getStrategy(location.getType());
        if (strategy.isReadable(userIdentity, location.getDir(), location.getFilename())) {
            return strategy;
        }
        throw new BusinessException(FssExceptionCodes.NO_READ_AUTHORITY, location);
    }

    @Override
    public FssFileMeta getMeta(I userIdentity, String locationUrl) {
        if (StringUtils.isNotBlank(locationUrl)) {
            FssFileLocation location = FssFileLocation.of(locationUrl);
            if (location != null) {
                FssServiceStrategy<I> strategy = validateUserRead(userIdentity, location);
                FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
                String storagePath = getStoragePath(strategy, location);
                FssFileDetail detail = accessor.getDetail(storagePath);
                if (detail != null) {
                    FssFileMeta meta = new FssFileMeta(locationUrl);
                    meta.setName(detail.getOriginalFilename());
                    meta.setReadUrl(getReadUrl(userIdentity, location, false));
                    meta.setThumbnailReadUrl(getReadUrl(userIdentity, location, true));
                    FssUploadLimit uploadLimit = strategy.getUploadLimit(userIdentity);
                    if (uploadLimit.isImageable()) {
                        meta.setImageable(true);
                        meta.setSize(ArrayUtil.get(uploadLimit.getSizes(), 0));
                    }
                    return meta;
                }
            }
        }
        return null;
    }

    @Override
    public FssFileDetail getDetail(I userIdentity, String locationUrl) {
        FssFileLocation location = FssFileLocation.of(locationUrl);
        if (location != null) {
            FssServiceStrategy<I> strategy = validateUserRead(userIdentity, location);
            FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
            String storagePath = getStoragePath(strategy, location);
            FssFileDetail detail = accessor.getDetail(storagePath);
            if (detail != null) {
                String downloadFilename = strategy.getDownloadFilename(userIdentity, location.getDir(),
                        location.getFilename());
                if (StringUtils.isNotBlank(downloadFilename)) {
                    detail = new FssFileDetail(downloadFilename, detail.getLastModifiedTime(), detail.getLength());
                }
            }
            return detail;
        }
        return null;
    }

    @Override
    public InputStream getReadStream(I userIdentity, String locationUrl) {
        Binate<String, FssStorageAccessor> binate = getStoragePathAndAccessorForRead(userIdentity, locationUrl);
        if (binate != null) {
            String storagePath = binate.getLeft();
            FssStorageAccessor accessor = binate.getRight();
            try {
                return accessor.getReadStream(storagePath);
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return null;
    }

    private Binate<String, FssStorageAccessor> getStoragePathAndAccessorForRead(I userIdentity, String locationUrl) {
        FssFileLocation location = FssFileLocation.of(locationUrl);
        if (location != null) {
            FssServiceStrategy<I> strategy = validateUserRead(userIdentity, location);
            String storagePath = getStoragePath(strategy, location);
            FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
            return new Binary<>(storagePath, accessor);
        }
        return null;
    }

    @Override
    public long read(I userIdentity, String locationUrl, OutputStream out, long offset, long expectedLength) {
        InputStream in = getReadStream(userIdentity, locationUrl);
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
    public String readText(I userIdentity, String locationUrl, long limit) {
        Binate<String, FssStorageAccessor> binate = getStoragePathAndAccessorForRead(userIdentity, locationUrl);
        if (binate != null) {
            String storagePath = binate.getLeft();
            FssStorageAccessor accessor = binate.getRight();
            InputStream in = null;
            try {
                in = accessor.getReadStream(storagePath);
                if (in != null) {
                    Charset charset = accessor.getCharset(storagePath);
                    if (charset == null) {
                        charset = FssUtil.getCharset(in);
                    }
                    if (charset == null) {
                        throw new BusinessException(FssExceptionCodes.IS_NOT_TEXT_FILE, locationUrl);
                    }
                    // 未指定读取限制，或文件大小未超过限制，才读取内容
                    if (limit <= 0 || in.available() <= limit) {
                        String encoding = charset.toString();
                        return IOUtils.toString(in, encoding);
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
        }
        return null;
    }

    @Override
    public void delete(I userIdentity, String locationUrl) {
        FssFileLocation location = FssFileLocation.of(locationUrl);
        if (location != null) {
            FssServiceStrategy<I> strategy = getStrategy(location.getType());
            if (!strategy.isDeletable(userIdentity, location.getDir(), location.getFilename())) {
                throw new BusinessException(FssExceptionCodes.NO_DELETE_AUTHORITY, location);
            }
            FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
            String storagePath = getStoragePath(strategy, location);
            accessor.delete(storagePath, strategy);
        }
    }

    @Override
    public String copy(I userIdentity, String sourceLocationUrl, String targetType, String targetScope) {
        FssFileLocation sourceLocation = FssFileLocation.of(sourceLocationUrl);
        if (sourceLocation != null) {
            FssServiceStrategy<I> sourceStrategy = validateUserRead(userIdentity, sourceLocation);
            FssServiceStrategy<I> targetStrategy = getStrategy(targetType);
            // 跨文件存储服务提供商无法复制（暂时没有跨文件存储服务提供商的需要，实际上也是可行的，只是性能较差，代码复杂）
            if (sourceStrategy.getProvider() != targetStrategy.getProvider()) {
                throw new BusinessException(FssExceptionCodes.CANNOT_COPY_BETWEEN_PROVIDERS);
            }
            // 获取目标存储目录，同时校验写权限
            String targetStorageDir = getStorageDirForWrite(targetStrategy, userIdentity, targetScope);

            FssStorageAccessor accessor = this.accessors.get(targetStrategy.getProvider());
            String sourceStoragePath = getStoragePath(sourceStrategy, sourceLocation);
            // 获取目标存储文件名
            FssFileDetail sourceDetail = accessor.getDetail(sourceStoragePath);
            String originalFilename = sourceDetail == null ? null : sourceDetail.getOriginalFilename();
            String targetStorageFilename = targetStrategy.getStorageFilename(userIdentity, targetScope,
                    originalFilename);
            if (targetStorageFilename.contains(FssServiceStrategy.PLACEHOLDER_MD5)) {
                // 需根据来源文件内容生成MD5形式的目标存储文件名，与write()时不同的是，来源输入流在读取后关闭，而不再继续使用
                try {
                    InputStream sourceInputStream = accessor.getReadStream(sourceStoragePath);
                    targetStorageFilename = targetStorageFilename.replace(FssServiceStrategy.PLACEHOLDER_MD5,
                            EncryptUtil.encryptByMd5(sourceInputStream));
                    sourceInputStream.close();
                } catch (IOException e) {
                    throw new BusinessException(FssExceptionCodes.CANNOT_COPY_WITHOUT_STORAGE_FILENAME_BY_SCOPE,
                            targetScope, targetType);
                }
            }
            // 目标文件扩展名与来源文件相同
            String extension = StringUtil.getExtension(sourceLocation.getFilename());
            if (StringUtils.isNotBlank(extension) && !extension.startsWith(Strings.DOT)) {
                extension = Strings.DOT + extension;
            }
            targetStorageFilename += extension;
            // 构建目标存储路径
            String targetStoragePath = getStoragePath(targetStorageDir, targetStorageFilename);
            // 构建目标定位路径
            String targetLocationPath = targetStrategy.getLocationPath(targetStoragePath);
            // 存储路径不同才有必要复制
            if (!sourceStoragePath.equals(targetStoragePath)) {
                accessor.copy(sourceStoragePath, targetStoragePath);
                targetStrategy.onWritten(userIdentity, targetLocationPath);
            }
            // 返回目标定位地址
            return FssFileLocation.toUrl(targetType, targetLocationPath);
        }
        return null;
    }

}
