package org.truenewx.tnxjeex.fss.service.aliyun;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjeex.fss.service.FssAccessor;
import org.truenewx.tnxjeex.fss.service.model.FssFileDetail;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * 阿里云的文件存储访问器
 *
 * @author jianglei
 */
public class AliyunFssAccessor implements FssAccessor {

    private AliyunAccount account;
    private FssAccessor delegate;
    private ExecutorService executorService;

    public AliyunFssAccessor(AliyunAccount account) {
        this.account = account;
    }

    public void setDelegate(FssAccessor delegate) {
        if (!(delegate instanceof AliyunFssAccessor)) {
            this.delegate = delegate;
        }
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public FssProvider getProvider() {
        return FssProvider.ALIYUN;
    }

    private String getBucketName() {
        return this.account.getOssBucket();
    }

    @Override
    public void write(InputStream in, String path, String filename) throws IOException {
        String originalPath = path;
        String originalFilename = filename;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        if (StringUtils.isNotBlank(filename)) {
            filename = EncryptUtil.encryptByBase64(filename); // 中文文件名会乱码导致签名校验失败
            objectMetadata.getUserMetadata().put("filename", filename);
        }
        path = AliyunOssUtil.standardizePath(path);
        this.account.getOssClient().putObject(getBucketName(), path, in, objectMetadata);

        if (this.delegate != null) {
            this.executorService.submit(() -> {
                try {
                    this.delegate.write(in, originalPath, originalFilename);
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            });
        }
    }

    private ObjectMetadata getObjectMetadata(String path) {
        path = AliyunOssUtil.standardizePath(path);
        return this.account.getOssClient().getObjectMetadata(getBucketName(), path);
    }

    @Override
    public FssFileDetail getDetail(String path) throws IOException {
        if (this.delegate != null) {
            FssFileDetail detail = this.delegate.getDetail(path);
            if (detail != null) {
                return detail;
            }
        }
        ObjectMetadata meta = getObjectMetadata(path);
        String filename = meta.getUserMetadata().get("filename");
        if (StringUtils.isNotBlank(filename)) {
            try {
                filename = EncryptUtil.decryptByBase64(filename);
            } catch (Exception ignored) {
            }
        }
        return new FssFileDetail(filename, meta.getLastModified().getTime(), meta.getContentLength());
    }

    @Override
    public Charset getCharset(String path) {
        ObjectMetadata meta = getObjectMetadata(path);
        return Charset.forName(meta.getContentEncoding());
    }

    @Override
    public InputStream getReadStream(String path) throws IOException {
        if (this.delegate != null) {
            InputStream readStream = this.delegate.getReadStream(path);
            if (readStream != null) {
                return readStream;
            }
        }
        try {
            path = AliyunOssUtil.standardizePath(path);
            return this.account.getOssClient().getObject(getBucketName(), path).getObjectContent();
        } catch (OSSException | ClientException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void delete(String path) {
        String originalPath = path;

        path = AliyunOssUtil.standardizePath(path);
        try {
            this.account.getOssClient().deleteObject(getBucketName(), path);
        } catch (Exception e) {
            LogUtil.warn(getClass(), e);
        }

        if (this.delegate != null) {
            this.executorService.submit(() -> {
                this.delegate.delete(originalPath);
            });
        }
    }

    @Override
    public void copy(String sourcePath, String targetPath) {
        String originalSourcePath = sourcePath;
        String originalTargetPath = targetPath;

        sourcePath = AliyunOssUtil.standardizePath(sourcePath);
        targetPath = AliyunOssUtil.standardizePath(targetPath);
        String bucketName = getBucketName();
        this.account.getOssClient().copyObject(bucketName, sourcePath, bucketName, targetPath);

        if (this.delegate != null) {
            this.executorService.submit(() -> {
                this.delegate.copy(originalSourcePath, originalTargetPath);
            });
        }
    }

}
