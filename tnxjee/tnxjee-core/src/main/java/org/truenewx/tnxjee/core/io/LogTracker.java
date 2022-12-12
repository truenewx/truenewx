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

    private RandomAccessFile accessFile;

    public LogTracker(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        this.accessFile = new RandomAccessFile(file, "r");
        seekEnd();
    }

    /**
     * 跳到日志文件最后
     *
     * @throws IOException 如果出现IO错误
     */
    public void seekEnd() throws IOException {
        this.accessFile.seek(this.accessFile.length());
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
        String line = this.accessFile.readLine();
        while (line != null && (size <= 0 || lines.size() <= size)) {
            // 去掉末尾的空白符，但不能去掉头部的
            line = (Strings.MINUS + line).trim().substring(1);
            line = new String(line.getBytes(StandardCharsets.ISO_8859_1), Strings.ENCODING_GB18030);
            lines.add(line);
            line = this.accessFile.readLine();
        }
        return lines;
    }

    @Override
    public void close() {
        try {
            this.accessFile.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
