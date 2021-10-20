package org.truenewx.tnxjee.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 加密输入流，配合读取 {@link EncryptOutputStream} 写入的附加信息
 *
 * @author jianglei
 */
public class EncryptInputStream extends InputStream {

    private InputStream in;
    private boolean readAttachment;
    private Byte salt;

    public EncryptInputStream(InputStream in, Byte salt) {
        this.in = in;
        this.salt = salt;
    }

    public synchronized String readAttachment() throws IOException {
        if (this.readAttachment) { // 已经读取附加信息，则不能再次读取
            return null;
        }
        // 先读取附加信息长度
        int length = readAttachmentLength();
        // 再读取附加信息
        byte[] bytes = new byte[length];
        this.in.read(bytes);
        this.readAttachment = true;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private int readAttachmentLength() throws IOException {
        byte[] bytes = new byte[4];
        this.in.read(bytes);
        return MathUtil.bytes2Int(bytes, 0);
    }

    @Override
    public int read() throws IOException {
        skipAttachment();
        int content = this.in.read();
        if (this.salt != null) {
            content ^= this.salt;
        }
        return content;
    }

    private synchronized void skipAttachment() throws IOException {
        if (!this.readAttachment) { // 如果此时还没读取头部附加信息，则跳过附加信息
            this.in.skip(readAttachmentLength());
            this.readAttachment = true;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        skipAttachment();
        return this.in.skip(n);
    }

    @Override
    public int available() throws IOException {
        skipAttachment();
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            skipAttachment();
            this.in.mark(readlimit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

}
