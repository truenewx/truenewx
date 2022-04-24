package org.truenewx.tnxjeex.fss.service.storage.own;

import java.io.*;

import org.truenewx.tnxjee.core.io.EncryptInputStream;
import org.truenewx.tnxjee.core.io.EncryptOutputStream;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 自有文件存储服务的文件加密访问流提供者
 */
public class EncryptOwnFssFileStreamProvider implements OwnFssFileStreamProvider {

    private Byte salt;

    public EncryptOwnFssFileStreamProvider() {
    }

    public EncryptOwnFssFileStreamProvider(byte salt) {
        this.salt = salt;
    }

    @Override
    public OutputStream getWriteStream(File file, String originalFilename) throws IOException {
        return new EncryptOutputStream(new FileOutputStream(file), originalFilename, this.salt);
    }

    @Override
    public String getOriginalFilename(File file) {
        try {
            EncryptInputStream in = new EncryptInputStream(new FileInputStream(file), this.salt);
            String filename = in.readAttachment();
            in.close();
            return filename;
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    @Override
    public InputStream getReadStream(File source) throws IOException {
        return new EncryptInputStream(new FileInputStream(source), this.salt);
    }

}
