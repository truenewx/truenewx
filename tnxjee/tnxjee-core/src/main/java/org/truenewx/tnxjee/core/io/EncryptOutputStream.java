package org.truenewx.tnxjee.core.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 加密输出流，配套使用 {@link EncryptInputStream} 读取附加信息
 *
 * @author jianglei
 */
public class EncryptOutputStream extends FileOutputStream {

    private FileOutputStream delegate;
    private Byte salt;

    public EncryptOutputStream(FileOutputStream delegate, String attachment, Byte salt) throws IOException {
        super(delegate.getFD());
        this.delegate = delegate;
        byte[] bytes = attachment == null ? new byte[0] : attachment.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        this.delegate.write(MathUtil.int2Bytes(length)); // 先写入4个字节的附加信息长度
        this.delegate.write(bytes); // 再写入附加信息

        this.salt = salt;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.salt != null) {
            b ^= this.salt;
        }
        this.delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.salt != null) {
            for (int i = off; i < off + len; i++) {
                b[i] ^= this.salt;
            }
        }
        this.delegate.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

    @Override
    public FileChannel getChannel() {
        return this.delegate.getChannel();
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

}
