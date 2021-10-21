package org.truenewx.tnxjeex.fss.service.own;

import java.io.*;

import org.truenewx.tnxjee.core.io.EncryptInputStream;
import org.truenewx.tnxjee.core.io.EncryptOutputStream;

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
    public OutputStream getOutputStream(String path, File target, String originalFilename) throws IOException {
        return new EncryptOutputStream(new FileOutputStream(target), originalFilename, this.salt);
    }

    @Override
    public String getOriginalFilename(String path, File file) throws IOException {
        EncryptInputStream in = new EncryptInputStream(new FileInputStream(file), this.salt);
        String filename = in.readAttachment();
        in.close();
        return filename;
    }

    @Override
    public InputStream getInputStream(File source) throws IOException {
        return new EncryptInputStream(new FileInputStream(source), this.salt);
    }

}