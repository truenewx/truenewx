package org.truenewx.tnxjeex.doc.ppt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * PPT文档工具类
 *
 * @author jianglei
 */
public class PptUtil {

    private PptUtil() {
    }

    private static final Properties MESSAGES = new Properties();

    static {
        Resource resource = IOUtil.findI18nResource("classpath:META-INF/message/tnxjeex-doc-ppt",
                Locale.getDefault(), FileExtensions.PROPERTIES);
        if (resource != null) {
            try {
                InputStream in = resource.getInputStream();
                MESSAGES.load(in);
                in.close();
            } catch (IOException e) {
                LogUtil.error(PptUtil.class, e);
            }
        }
    }

    public static String getMessage(String key) {
        return MESSAGES.getProperty(key);
    }

}
