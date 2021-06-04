package org.truenewx.tnxjeex.fss.service.aliyun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjeex.fss.service.FssAccessor;
import org.truenewx.tnxjeex.fss.service.model.FssProvider;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * 阿里云的文件存储访问器
 *
 * @author jianglei
 */
public class AliyunFssAccessor implements FssAccessor {

    private AliyunAccount account;

    public AliyunFssAccessor(AliyunAccount account) {
        this.account = account;
    }

    @Override
    public FssProvider getProvider() {
        return FssProvider.ALIYUN;
    }

    @Override
    public void write(InputStream in, String path, String filename) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        if (StringUtils.isNotBlank(filename)) {
            filename = EncryptUtil.encryptByBase64(filename); // 中文文件名会乱码导致签名校验失败
            objectMetadata.getUserMetadata().put("filename", filename);
        }
        path = AliyunOssUtil.standardizePath(path);
        this.account.getOssClient().putObject(this.account.getOssBucket(), path, in, objectMetadata);
    }

    @Override
    public String getOriginalFilename(String path) {
        try {
            path = AliyunOssUtil.standardizePath(path);
            ObjectMetadata meta = this.account.getOssClient().getObjectMetadata(this.account.getOssBucket(), path);
            String filename = meta.getUserMetadata().get("filename");
            if (StringUtils.isNotBlank(filename)) {
                try {
                    filename = EncryptUtil.decryptByBase64(filename);
                } catch (Exception ignored) {
                }
            }
            return filename;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Long getLastModifiedTime(String path) {
        try {
            path = AliyunOssUtil.standardizePath(path);
            ObjectMetadata meta = this.account.getOssClient().getObjectMetadata(this.account.getOssBucket(), path);
            return meta.getLastModified().getTime();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean read(String path, OutputStream out) throws IOException {
        try {
            path = AliyunOssUtil.standardizePath(path);
            InputStream in = this.account.getOssClient().getObject(this.account.getOssBucket(), path)
                    .getObjectContent();
            IOUtils.copy(in, out);
            in.close();
            return true;
        } catch (ClientException e) {
            return false;
        }
    }

}
