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
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.*;
import org.truenewx.tnxjee.core.util.function.TrPredicate;
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

    private static final String FILE_STATE_PENDING = "pending";
    private static final String FILE_STATE_DELETING = "deleting";

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
        String storageFilename = strategy.getStorageFilename(userIdentity, scope,
                StringUtil.excludeExtension(originalFilename));
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
        String storagePath = getWriteStoragePath(storageDir, storageFilename);
        // 写文件
        write(strategy, storagePath, originalFilename, in);

        FssStorageProvider provider = strategy.getProvider();
        // 写好文件之后，如果服务策略是公开匿名可读，则还需要进行相应授权，不过本地自有提供商无需进行授权
        if (strategy.isPublicReadable() && provider != FssStorageProvider.OWN) {
            FssStorageAuthorizer authorizer = this.authorizers.get(provider);
            authorizer.authorizePublicRead(storagePath);
        }
        // 构建定位地址
        String locationPath = strategy.getLocationPath(storagePath);
        strategy.onWritten(userIdentity, locationPath);
        return FssFileLocation.toUrl(type, locationPath, false);
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
        // 定位地址尚未构建，无法在线程中根据定位地址判断权限
        if (storageDir == null) {
            throw new BusinessException(FssExceptionCodes.NO_WRITE_AUTHORITY);
        }
        return strategy.getStorageRootDir() + NetUtil.standardizeUrl(storageDir);
    }

    private String getWriteStoragePath(String storageDir, String storageFilename) {
        String storagePath = storageDir + Strings.SLASH + storageFilename;
        return cleanStoragePath(storagePath, false);
    }

    /**
     * 清理存储路径，确保路径合法
     *
     * @param storagePath 存储路径
     * @param nullable    可否为null，true-则存储路径是事务中已标记为删除中时返回null，false-一般在写入操作时，无论是否标记为删除中，仍需要返回正确的存储地址
     * @return 清理后的存储路径
     */
    private String cleanStoragePath(String storagePath, boolean nullable) {
        storagePath = storagePath.replaceAll("[+%]", Strings.SPACE); // 因长度为关键的判断依据，故需保证长度不变
        if (isTransactional()) { // 如果位于事务中，则根据事务绑定状态判断存储路径是否处理中
            String state = (String) TransactionSynchronizationManager.getResource(storagePath);
            if (state != null) {
                // 可以为null且正在删除中，则返回null表示不存在
                if (nullable && FILE_STATE_DELETING.equals(state)) {
                    return null;
                }
                // 正在准备中，则返回准备中的存储路径作为替代
                if (state.endsWith(Strings.DOT + FILE_STATE_PENDING)) {
                    return state;
                }
            }
        }
        return storagePath;
    }

    private void write(FssServiceStrategy<I> strategy, String storagePath, String originalFilename, InputStream in)
            throws IOException {
        FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
        // 如果位于事务之中，则先写入临时文件，再执行提交或回滚处理，暂时只支持对文件处理的 Read_Commit 这一种事务隔离级别
        if (isTransactional()) {
            String pendingStoragePath = storagePath + Strings.DOT + FILE_STATE_PENDING;
            accessor.write(in, pendingStoragePath, originalFilename);

            // 在当前事务中标记实际存储路径
            TransactionSynchronizationManager.bindResource(storagePath, pendingStoragePath);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    // 提交时临时文件迁移为正式文件
                    accessor.move(pendingStoragePath, storagePath);
                }

                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) { // 回滚时删除临时文件
                        accessor.delete(pendingStoragePath, strategy);
                    }
                    // 事务结束时移除文件标记
                    TransactionSynchronizationManager.unbindResource(storagePath);
                }
            });
        } else { // 如果不位于事务之中，则直接写入文件
            accessor.write(in, storagePath, originalFilename);
        }
    }

    private boolean isTransactional() {
        Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
        // 默认隔离级别与数据库相关，但至少也是Read_Commit
        return isolationLevel != null && isolationLevel != TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
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

            if (!FssUtil.isWriteableInThread(locationUrl)
                    && !strategy.isWriteable(userIdentity, location.getDir(), location.getFilename())) {
                throw new BusinessException(FssExceptionCodes.NO_WRITE_AUTHORITY);
            }

            String storagePath = getReadStoragePath(strategy, location);
            if (storagePath != null) {
                write(strategy, storagePath, originalFilename, in);
            }
        }
    }

    @Nullable
    private String getReadStoragePath(FssServiceStrategy<I> strategy, FssFileLocation location) {
        String storagePath = strategy.getStoragePath(location.getPath());
        return cleanStoragePath(storagePath, true);
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
                // 本地自有提供商的读取URL取定位相对地址
                return location.getRelativeUrl();
            } else {
                FssStorageAuthorizer authorizer = this.authorizers.get(provider);
                String storagePath = getReadStoragePath(strategy, location);
                if (storagePath != null) {
                    if (thumbnail) {
                        storagePath = appendThumbnailParameters(strategy, storagePath);
                    }
                    return authorizer.getReadUrl(userIdentity, storagePath);
                }
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
        if (FssUtil.isReadableInThread(location.toString())
                || strategy.isReadable(userIdentity, location.getDir(), location.getFilename())) {
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
                String storagePath = getReadStoragePath(strategy, location);
                if (storagePath != null) {
                    FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
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
        }
        return null;
    }

    @Override
    public FssFileDetail getDetail(I userIdentity, String locationUrl) {
        FssFileLocation location = FssFileLocation.of(locationUrl);
        if (location != null) {
            FssServiceStrategy<I> strategy = validateUserRead(userIdentity, location);
            String storagePath = getReadStoragePath(strategy, location);
            if (storagePath != null) {
                FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
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
            String storagePath = getReadStoragePath(strategy, location);
            if (storagePath != null) {
                FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
                return new Binary<>(storagePath, accessor);
            }
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
            if (!FssUtil.isWriteableInThread(locationUrl)
                    && !strategy.isDeletable(userIdentity, location.getDir(), location.getFilename())) {
                throw new BusinessException(FssExceptionCodes.NO_DELETE_AUTHORITY, location);
            }
            FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
            String storagePath = getReadStoragePath(strategy, location);
            if (storagePath != null) {
                if (isTransactional()) {
                    // 如果位于写事务之中，则暂不实际删除，只在当前事务中标记状态为删除中，而在提交事务时删除，否则直接删除
                    TransactionSynchronizationManager.bindResource(storagePath, FILE_STATE_DELETING);
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void beforeCommit(boolean readOnly) {
                            // 提交事务则执行文件删除
                            accessor.delete(storagePath, strategy);
                        }

                        @Override
                        public void afterCompletion(int status) {
                            // 事务结束时移除文件标记
                            TransactionSynchronizationManager.unbindResource(storagePath);
                        }
                    });
                } else {
                    accessor.delete(storagePath, strategy);
                }
            }
        }
    }

    @Override
    public String copy(I userIdentity, String sourceLocationUrl, String targetType, String targetScope) {
        return copyOrMove(userIdentity, sourceLocationUrl, targetType, targetScope, true);
    }

    private String copyOrMove(I userIdentity, String sourceLocationUrl, String targetType, String targetScope,
            boolean copy) {
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
            String sourceStoragePath = getReadStoragePath(sourceStrategy, sourceLocation);
            if (sourceStoragePath != null) {
                // 获取目标存储文件名
                FssFileDetail sourceDetail = accessor.getDetail(sourceStoragePath);
                String originalFilename = sourceDetail == null ? null : sourceDetail.getOriginalFilename();
                String targetStorageFilename = targetStrategy.getStorageFilename(userIdentity, targetScope,
                        StringUtil.excludeExtension(originalFilename));
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
                String targetStoragePath = getWriteStoragePath(targetStorageDir, targetStorageFilename);
                // 构建目标定位路径
                String targetLocationPath = targetStrategy.getLocationPath(targetStoragePath);
                // 存储路径不同才有必要复制
                if (!sourceStoragePath.equals(targetStoragePath)) {
                    if (isTransactional()) {
                        // 如果位于事务之中，则先复制来源文件至临时目标文件
                        String pendingTargetStoragePath = targetStoragePath + Strings.DOT + FILE_STATE_PENDING;
                        accessor.copy(sourceStoragePath, pendingTargetStoragePath);
                        // 在当前事务中标记目标文件实际存储路径
                        TransactionSynchronizationManager.bindResource(targetStoragePath, pendingTargetStoragePath);
                        if (!copy) { // 迁移操作中，在当前事务中标记来源文件为删除中状态
                            TransactionSynchronizationManager.bindResource(sourceStoragePath, FILE_STATE_DELETING);
                        }
                        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                            @Override
                            public void beforeCommit(boolean readOnly) {
                                // 提交时临时目标文件迁移为正式目标文件
                                accessor.move(pendingTargetStoragePath, targetStoragePath);
                                if (!copy) { // 迁移操作中，删除来源文件
                                    accessor.delete(sourceStoragePath, sourceStrategy);
                                }
                            }

                            @Override
                            public void afterCompletion(int status) {
                                if (status != STATUS_COMMITTED) { // 回滚时删除临时目标文件
                                    accessor.delete(pendingTargetStoragePath, targetStrategy);
                                }
                                // 事务结束时移除文件标记
                                TransactionSynchronizationManager.unbindResource(sourceStoragePath);
                                TransactionSynchronizationManager.unbindResource(targetStoragePath);
                            }
                        });
                    } else { // 不在事务中，则直接执行
                        if (copy) {
                            accessor.copy(sourceStoragePath, targetStoragePath);
                        } else {
                            accessor.move(sourceStoragePath, targetStoragePath);
                        }
                    }
                    targetStrategy.onWritten(userIdentity, targetLocationPath);
                }
                // 返回目标定位地址
                return FssFileLocation.toUrl(targetType, targetLocationPath, false);
            }
        }
        return null;
    }

    @Override
    public String move(I userIdentity, String sourceLocationUrl, String targetType, String targetScope) {
        return copyOrMove(userIdentity, sourceLocationUrl, targetType, targetScope, false);
    }

    @Override
    public long getTotalSize(I userIdentity, String[] types) {
        long total = 0;
        if (types != null) {
            for (String type : types) {
                FssServiceStrategy<I> strategy = this.strategies.get(type);
                if (strategy != null) {
                    FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
                    String storageDir = strategy.getStorageRootDir() + strategy.getStorageRelativeDir(userIdentity,
                            null);
                    total += accessor.getTotalSize(storageDir);
                }
            }
        }
        return total;
    }

    @Override
    public void loopReadStream(I userIdentity, String[] types, TrPredicate<String, Long, InputStream> predicate) {
        if (types != null) {
            for (String type : types) {
                FssServiceStrategy<I> strategy = this.strategies.get(type);
                if (strategy != null) {
                    FssStorageAccessor accessor = this.accessors.get(strategy.getProvider());
                    String storageDir = strategy.getStorageRootDir() + strategy.getStorageRelativeDir(userIdentity,
                            null);
                    accessor.loopReadStream(storageDir, predicate);
                }
            }
        }
    }

}
