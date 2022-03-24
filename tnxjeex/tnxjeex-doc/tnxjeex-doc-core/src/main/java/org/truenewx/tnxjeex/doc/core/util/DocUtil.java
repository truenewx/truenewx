package org.truenewx.tnxjeex.doc.core.util;

import org.truenewx.tnxjee.core.Strings;

/**
 * 文档处理工具类
 *
 * @author jianglei
 */
public class DocUtil {

    private DocUtil() {
    }

    public static String standardizeExtension(String extension) {
        if (extension.startsWith(Strings.DOT)) {
            extension = extension.substring(1);
        }
        return extension.toLowerCase();
    }

}
