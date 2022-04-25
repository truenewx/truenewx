package org.truenewx.tnxjeex.fss.service.util;

import java.io.*;
import java.nio.charset.Charset;

import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

import info.monitorenter.cpdetector.io.*;

/**
 * FSS工具类
 */
public class FssUtil {

    private FssUtil() {
    }

    private static final CodepageDetectorProxy DETECTOR = CodepageDetectorProxy.getInstance();

    static {
        DETECTOR.add(new ParsingDetector(false));
        DETECTOR.add(new ByteOrderMarkDetector());
        DETECTOR.add(JChardetFacade.getInstance());
        DETECTOR.add(ASCIIDetector.getInstance());
        DETECTOR.add(UnicodeDetector.getInstance());
    }

    public static Charset getCharset(File file) {
        if (file != null && file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                return getCharset(in);
            } catch (IOException e) {
                LogUtil.error(FssUtil.class, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        LogUtil.error(FssUtil.class, e);
                    }
                }
            }
        }
        return null;
    }

    public static Charset getCharset(InputStream in) {
        try {
            if (IOUtil.isBinary(in)) {
                return null;
            }
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            int size = IOUtil.DEFAULT_BUFFER_SIZE; // 最多尝试4KB，以免耗时太长
            in.mark(size);
            Charset charset = DETECTOR.detectCodepage(in, size);
            in.reset();
            if (charset instanceof UnknownCharset || charset instanceof UnsupportedCharset) {
                return null;
            }
            return charset;
        } catch (IOException e) {
            LogUtil.error(FssUtil.class, e);
        }
        return null;
    }

}
