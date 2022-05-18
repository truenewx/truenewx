package org.truenewx.tnxjee.core.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 加密输入流，配合读取 {@link EncryptOutputStream} 写入的附加信息
 *
 * @author jianglei
 */
public class EncryptInputStream extends FileInputStream {

    private FileInputStream delegate;
    private boolean readAttachment;
    private Byte salt;

    public EncryptInputStream(FileInputStream delegate, Byte salt) throws IOException {
        super(delegate.getFD());
        this.delegate = delegate;
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
        this.delegate.read(bytes);
        this.readAttachment = true;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private int readAttachmentLength() throws IOException {
        byte[] bytes = new byte[4];
        this.delegate.read(bytes);
        return MathUtil.bytes2Int(bytes, 0);
    }

    @Override
    public int read() throws IOException {
        skipAttachment();
        int content = this.delegate.read();
        if (this.salt != null) {
            content ^= this.salt;
        }
        return content;
    }

    private synchronized void skipAttachment() throws IOException {
        if (!this.readAttachment) { // 如果此时还没读取头部附加信息，则跳过附加信息
            this.delegate.skip(readAttachmentLength());
            this.readAttachment = true;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        skipAttachment();
        return this.delegate.skip(n);
    }

    @Override
    public int available() throws IOException {
        skipAttachment();
        return this.delegate.available();
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            skipAttachment();
            this.delegate.mark(readlimit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return this.delegate.markSupported();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.delegate.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.delegate.read(b, off, len);
    }

    @Override
    public FileChannel getChannel() {
        return this.delegate.getChannel();
    }

}
