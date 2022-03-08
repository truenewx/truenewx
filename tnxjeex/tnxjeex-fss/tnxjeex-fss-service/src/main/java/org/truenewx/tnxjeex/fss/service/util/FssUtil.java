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
            try {
                return getCharset(new FileInputStream(file));
            } catch (IOException e) {
                LogUtil.error(FssUtil.class, e);
            }
        }
        return null;
    }

    public static Charset getCharset(InputStream in) {
        try {
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            if (IOUtil.isBinary(in)) {
                return null;
            }
            Charset charset = DETECTOR.detectCodepage(in, IOUtil.DEFAULT_BUFFER_SIZE); // 最多尝试4KB，以免耗时太长
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
