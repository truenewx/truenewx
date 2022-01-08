package org.truenewx.tnxjeex.fss.service.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

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
                return DETECTOR.detectCodepage(file.toURI().toURL());
            } catch (IOException e) {
                LogUtil.error(FssUtil.class, e);
            }
        }
        return null;
    }

}
