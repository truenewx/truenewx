package org.truenewx.tnxjee.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.truenewx.tnxjee.core.util.MathUtil;

/**
 * 带有附加信息的输出流，配套使用 {@link AttachInputStream} 读取附加信息
 *
 * @author jianglei
 */
public class AttachOutputStream extends OutputStream {

    private OutputStream out;
    private Byte salt;

    public AttachOutputStream(OutputStream out, String attachment, Byte salt) throws IOException {
        this.out = out;
        byte[] bytes = attachment == null ? new byte[0] : attachment.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        this.out.write(MathUtil.int2Bytes(length)); // 先写入4个字节的附加信息长度
        this.out.write(bytes); // 再写入附加信息

        this.salt = salt;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.salt != null) {
            b ^= this.salt;
        }
        this.out.write(b);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

}
