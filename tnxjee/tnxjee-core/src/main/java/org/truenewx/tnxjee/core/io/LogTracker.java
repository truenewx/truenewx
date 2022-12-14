package org.truenewx.tnxjee.core.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 日志追踪器
 *
 * @author jianglei
 */
public class LogTracker implements Closeable {

    private File file;
    private RandomAccessFile raf;
    private String encoding = Strings.ENCODING_UTF8;

    public LogTracker(File file) {
        this.file = file;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 跳到日志文件最后
     *
     * @throws IOException 如果出现IO错误
     */
    public void seekEnd() throws IOException {
        if (this.raf != null) {
            this.raf.seek(this.raf.length());
        }
    }

    private boolean prepare() throws IOException {
        if (this.raf == null && this.file != null && this.file.exists()) {
            this.raf = new RandomAccessFile(this.file, "r");
            seekEnd();
        }
        return this.raf != null;
    }

    /**
     * 获取跟踪的日志行清单
     *
     * @param size 一次性最多获取的行数，<=0表示不限
     * @return 日志行清单
     * @throws IOException 如果读取过程中出现IO错误
     */
    public List<String> track(int size) throws IOException {
        List<String> lines = new ArrayList<>();
        if (this.prepare()) {
            String line = this.raf.readLine();
            while (line != null && (size <= 0 || lines.size() <= size)) {
                // 去掉末尾的空白符，但不能去掉头部的
                line = (Strings.MINUS + line).trim().substring(1);
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), this.encoding);
                lines.add(line);
                line = this.raf.readLine();
            }
        }
        return lines;
    }

    @Override
    public void close() {
        if (this.raf != null) {
            try {
                this.raf.close();
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
    }

}
