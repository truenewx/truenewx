package org.truenewx.tnxjeex.fss.service.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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

    private static final ThreadLocal<Map<String, Boolean>> AUTHORITIES = new ThreadLocal<>();

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

    /**
     * 设置在当前线程的后续处理中当前用户对指定文件可写，避免重复校验写入权限<br>
     * 注意：一旦在请求中调用本方法，则务必在请求结束前调用{@link #clearAuthorityInThread}()方法清除线程缓存，否则会导致内存溢出
     *
     * @param locationUrl 文件的定位地址
     */
    public static void setWriteableInThread(String locationUrl) {
        Map<String, Boolean> authorities = AUTHORITIES.get();
        if (authorities == null) {
            authorities = new HashMap<>();
            AUTHORITIES.set(authorities);
        }
        authorities.put(locationUrl, Boolean.TRUE);
    }

    /**
     * 判断在当前线程中的当前用户对指定文件是否可写
     *
     * @param locationUrl 文件的定位地址
     * @return 在当前线程中的当前用户对指定文件是否可写
     */
    public static boolean isWriteableInThread(String locationUrl) {
        Map<String, Boolean> authorities = AUTHORITIES.get();
        if (authorities != null) {
            Boolean writeable = authorities.get(locationUrl);
            return writeable == Boolean.TRUE;
        }
        return false;
    }

    /**
     * 设置在当前线程的后续处理中当前用户对指定文件可读，避免重复校验读取权限<br>
     * 注意：一旦在请求中调用本方法，则务必在请求结束前调用{@link #clearAuthorityInThread}()方法清除线程缓存，否则会导致内存溢出
     *
     * @param locationUrl 文件的定位地址
     */
    public static void setReadableInThread(String locationUrl) {
        Map<String, Boolean> authorities = AUTHORITIES.get();
        if (authorities == null) {
            authorities = new HashMap<>();
            AUTHORITIES.set(authorities);
        }
        authorities.putIfAbsent(locationUrl, Boolean.FALSE);
    }

    /**
     * 判断在当前线程中的当前用户对指定文件是否可读
     *
     * @param locationUrl 文件的定位地址
     * @return 在当前线程中的当前用户对指定文件是否可读
     */
    public static boolean isReadableInThread(String locationUrl) {
        Map<String, Boolean> authorities = AUTHORITIES.get();
        if (authorities != null) {
            Boolean writeable = authorities.get(locationUrl);
            return writeable != null;
        }
        return false;
    }

    /**
     * 清除当前线程中设置的当前用户具有的所有文件访问权限
     */
    public static void clearAuthorityInThread() {
        AUTHORITIES.remove();
    }

}
