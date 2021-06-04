package org.truenewx.tnxjeex.fss.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.spec.upload.FileUploadLimit;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;
import org.truenewx.tnxjeex.fss.service.model.FssStoragePath;

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
    public void afterInitialized(ApplicationContext context) throws Exception {
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
    public FileUploadLimit getUploadLimit(String type, I userIdentity) {
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
    public String write(String type, String scope, I userIdentity, long fileSize, String filename, InputStream in)
            throws IOException {
        FssAccessStrategy<I> strategy = getStrategy(type);
        // 上传限制校验
        FileUploadLimit uploadLimit = strategy.getUploadLimit(userIdentity);
        String extension = uploadLimit.validate(fileSize, filename);
        // 获取相对目录，同时校验写权限
        String relativeDir = strategy.getRelativeDir(scope, userIdentity);
        if (relativeDir == null) {
            throw new BusinessException(FssExceptionCodes.NO_WRITE_AUTHORITY);
        }
        // 用BufferedInputStream装载以确保输入流可以标记和重置位置
        in = new BufferedInputStream(in);
        in.mark(Integer.MAX_VALUE);
        String md5Code = EncryptUtil.encryptByMd5(in);
        in.reset();
        String storageFilename = md5Code + extension; // 存储文件名
        FssStoragePath fsp = new FssStoragePath(type, NetUtil.standardizeUrl(relativeDir), storageFilename);
        String contextPath = NetUtil.standardizeUrl(strategy.getContextPath());
        String storagePath = contextPath + fsp.getRelativePath();

        FssProvider provider = strategy.getProvider();
        FssAccessor accessor = this.accessors.get(provider);
        accessor.write(in, storagePath, filename);
        // 写好文件之后，如果访问策略是公开匿名可读，则还需要进行相应授权，不过本地自有提供商无需进行授权
        if (strategy.isPublicReadable() && provider != FssProvider.OWN) {
            FssAuthorizer authorizer = this.authorizers.get(provider);
            authorizer.authorizePublicRead(storagePath);
        }
        return fsp.getUrl();
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
                String path = strategy.getContextPath() + fsp.getRelativePath();
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

    private FssAccessStrategy<I> validateUserRead(I userIdentity, FssStoragePath fsp) {
        if (fsp.isValid()) {
            FssAccessStrategy<I> strategy = this.strategies.get(fsp.getType());
            if (strategy != null && strategy.isReadable(userIdentity, fsp.getRelativeDir())) {
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
                String path = strategy.getContextPath() + fsp.getRelativePath();
                String filename = accessor.getOriginalFilename(path);
                if (filename != null) {
                    String thumbnailReadUrl = getReadUrl(userIdentity, fsp, true);
                    String readUrl = getReadUrl(userIdentity, fsp, false);
                    return new FssFileMeta(filename, storageUrl, readUrl, thumbnailReadUrl);
                }
            }
        }
        return null;
    }

    @Override
    public Long getLastModifiedTime(I userIdentity, String path) {
        path = NetUtil.standardizeUrl(path);
        FssStoragePath fsp = FssStoragePath.of(path);
        if (fsp != null) {
            FssAccessStrategy<I> strategy = validateUserRead(userIdentity, fsp);
            FssAccessor accessor = this.accessors.get(strategy.getProvider());
            path = strategy.getContextPath() + fsp.getRelativePath();
            return accessor.getLastModifiedTime(path);
        }
        return null;
    }

    @Override
    public void read(I userIdentity, String path, OutputStream out) throws IOException {
        path = NetUtil.standardizeUrl(path);
        FssStoragePath fsp = FssStoragePath.of(path);
        if (fsp != null) {
            FssAccessStrategy<I> strategy = validateUserRead(userIdentity, fsp);
            FssAccessor accessor = this.accessors.get(strategy.getProvider());
            path = strategy.getContextPath() + fsp.getRelativePath();
            accessor.read(path, out);
        }
    }

}
