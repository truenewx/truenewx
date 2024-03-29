package org.truenewx.tnxjeex.fss.service.storage.aliyun;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.function.TrPredicate;
import org.truenewx.tnxjeex.fss.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.service.FssDirDeletePredicate;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageAccessor;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageProvider;
import org.truenewx.tnxjeex.fss.service.util.FssUtil;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

/**
 * 阿里云的文件存储访问器
 *
 * @author jianglei
 */
public class AliyunFssStorageAccessor implements FssStorageAccessor {

    private AliyunAccount account;
    private FssStorageAccessor delegate;
    private ExecutorService executorService;

    public AliyunFssStorageAccessor(AliyunAccount account) {
        this.account = account;
    }

    public void setDelegate(FssStorageAccessor delegate) {
        if (!(delegate instanceof AliyunFssStorageAccessor)) {
            this.delegate = delegate;
        }
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public FssStorageProvider getProvider() {
        return FssStorageProvider.ALIYUN;
    }

    private String getBucketName() {
        return this.account.getOssBucket();
    }

    @Override
    public void write(InputStream in, String storagePath, String originalFilename) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        if (StringUtils.isNotBlank(originalFilename)) {
            String base64Filename = EncryptUtil.encryptByBase64(originalFilename); // 中文文件名会乱码导致签名校验失败
            objectMetadata.getUserMetadata().put("filename", base64Filename);
        }
        Charset charset = FssUtil.getCharset(in);
        if (charset != null) {
            objectMetadata.setContentEncoding(charset.name());
        }
        String standardizedStoragePath = AliyunOssUtil.standardizePath(storagePath);
        this.account.getOssClient().putObject(getBucketName(), standardizedStoragePath, in, objectMetadata);

        if (this.delegate != null) {
            this.executorService.submit(() -> {
                try {
                    this.delegate.write(in, storagePath, originalFilename);
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            });
        }
    }

    private ObjectMetadata getObjectMetadata(String path) {
        path = AliyunOssUtil.standardizePath(path);
        try {
            return this.account.getOssClient().getObjectMetadata(getBucketName(), path);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    @Override
    public FssFileDetail getDetail(String storagePath) {
        if (this.delegate != null) {
            FssFileDetail detail = this.delegate.getDetail(storagePath);
            if (detail != null) {
                return detail;
            }
        }
        ObjectMetadata meta = getObjectMetadata(storagePath);
        if (meta != null) {
            String filename = meta.getUserMetadata().get("filename");
            if (StringUtils.isNotBlank(filename)) {
                try {
                    filename = EncryptUtil.decryptByBase64(filename);
                } catch (Exception ignored) {
                }
            }
            return new FssFileDetail(filename, meta.getLastModified().getTime(), meta.getContentLength());
        }
        return null;
    }

    @Override
    public Charset getCharset(String storagePath) {
        ObjectMetadata meta = getObjectMetadata(storagePath);
        if (meta != null) {
            String encoding = meta.getContentEncoding();
            if (encoding != null) {
                try {
                    return Charset.forName(encoding);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    @Override
    public InputStream getReadStream(String storagePath) throws IOException {
        if (this.delegate != null) {
            InputStream readStream = this.delegate.getReadStream(storagePath);
            if (readStream != null) {
                return readStream;
            }
        }
        try {
            storagePath = AliyunOssUtil.standardizePath(storagePath);
            return this.account.getOssClient().getObject(getBucketName(), storagePath).getObjectContent();
        } catch (OSSException | ClientException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void delete(String storagePath, FssDirDeletePredicate dirDeletePredicate) {
        OSS oss = this.account.getOssClient();
        String bucketName = getBucketName();
        String standardizedPath = AliyunOssUtil.standardizePath(storagePath);
        try {
            String filename = standardizedPath.substring(standardizedPath.lastIndexOf(Strings.SLASH) + 1);
            // 支持按文件名中的通配符（不支持路径中的）删除
            int index = filename.indexOf(Strings.ASTERISK);
            if (index >= 0) {
                String prefix = standardizedPath.substring(0, index); // 路径中首个通配符之前的部分均作为前缀查找匹配对象
                ListObjectsV2Result result = oss.listObjectsV2(bucketName, prefix);
                List<String> deletedKeys = new ArrayList<>();
                for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
                    String key = objectSummary.getKey();
                    if (StringUtil.wildcardMatch(key, standardizedPath)) {
                        deletedKeys.add(key);
                    }
                }
                if (deletedKeys.size() > 0) {
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
                    deleteObjectsRequest.setKeys(deletedKeys);
                    oss.deleteObjects(deleteObjectsRequest);
                }
            } else {
                oss.deleteObject(bucketName, standardizedPath);
            }
        } catch (Exception e) {
            LogUtil.warn(getClass(), e);
        }

        if (this.delegate != null) {
            this.executorService.submit(() -> {
                this.delegate.delete(storagePath, dirDeletePredicate);
            });
        }
    }

    @Override
    public void copy(String sourceStoragePath, String targetStoragePath) {
        String originalSourcePath = sourceStoragePath;
        String originalTargetPath = targetStoragePath;

        sourceStoragePath = AliyunOssUtil.standardizePath(sourceStoragePath);
        targetStoragePath = AliyunOssUtil.standardizePath(targetStoragePath);
        String bucketName = getBucketName();
        this.account.getOssClient().copyObject(bucketName, sourceStoragePath, bucketName, targetStoragePath);

        if (this.delegate != null) {
            this.executorService.submit(() -> {
                this.delegate.copy(originalSourcePath, originalTargetPath);
            });
        }
    }

    @Override
    public void move(String sourceStoragePath, String targetStoragePath) {
        sourceStoragePath = AliyunOssUtil.standardizePath(sourceStoragePath);
        targetStoragePath = AliyunOssUtil.standardizePath(targetStoragePath);
        OSS client = this.account.getOssClient();
        String bucketName = getBucketName();
        client.copyObject(bucketName, sourceStoragePath, bucketName, targetStoragePath);
        client.deleteObject(bucketName, sourceStoragePath);
    }

    @Override
    public long getTotalSize(String storageDir) {
        storageDir = AliyunOssUtil.standardizePath(storageDir);
        long total = 0;
        ObjectListing objects = this.account.getOssClient().listObjects(getBucketName(), storageDir);
        List<OSSObjectSummary> objectSummaries = objects.getObjectSummaries();
        for (OSSObjectSummary objectSummary : objectSummaries) {
            total += objectSummary.getSize();
        }
        return total;
    }

    @Override
    public void loopReadStream(String storageDir, TrPredicate<String, Long, InputStream> predicate) {
        storageDir = AliyunOssUtil.standardizePath(storageDir);
        OSS client = this.account.getOssClient();
        ObjectListing objects = client.listObjects(getBucketName(), storageDir);
        List<OSSObjectSummary> objectSummaries = objects.getObjectSummaries();
        for (OSSObjectSummary objectSummary : objectSummaries) {
            long size = objectSummary.getSize();
            String storagePath = objectSummary.getKey();
            InputStream in = client.getObject(getBucketName(), storagePath).getObjectContent();
            // 在此调用in.close()会导致后续输入流读取超时
            if (!predicate.test(storagePath, size, in)) {
                break;
            }
        }
    }

}
