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
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                Charset charset = DETECTOR.detectCodepage(in, 4096); // 最多尝试4096B，以免耗时太长
                if (charset != null) {
                    if (charset instanceof UnknownCharset || charset instanceof UnsupportedCharset) {
                        return null;
                    }
                    if (IOUtil.isBinary(in)) {
                        return null;
                    }
                }
                return charset;
            } catch (IOException e) {
                LogUtil.error(FssUtil.class, e);
            }
        }
        return null;
    }

}
