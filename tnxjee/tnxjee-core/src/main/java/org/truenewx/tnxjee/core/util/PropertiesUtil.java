package org.truenewx.tnxjee.core.util;

import java.io.*;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 属性集工具类
 */
public class PropertiesUtil {

    private PropertiesUtil() {
    }

    public static void load(Resource source, Properties target) {
        try {
            // 资源的exists()判断可能导致资源锁定，即使下面in.close()也无法解锁，故不进行存在与否的判断
            InputStream in = source.getInputStream();
            target.load(in);
            in.close();
        } catch (IOException e) {
            logException(e);
        }
    }

    private static void logException(IOException e) {
        LogUtil.warn(PropertiesUtil.class, e.getClass().getName() + Strings.COLON + Strings.SPACE + e.getMessage());
    }

    public static void load(File source, Properties target) {
        try {
            if (source.exists()) {
                InputStream in = new FileInputStream(source);
                target.load(in);
                in.close();
            }
        } catch (IOException e) {
            logException(e);
        }
    }

    public static void load(String sourceLocation, Properties target) throws IOException {
        if (sourceLocation.startsWith(IOUtil.JAR_FILE_URL_PREFIX)) {
            UrlResource source = new UrlResource(sourceLocation);
            load(source, target);
        } else {
            File source = ResourceUtils.getFile(sourceLocation);
            load(source, target);
        }
    }

    public static Properties load(String sourceLocation) throws IOException {
        Properties properties = new Properties();
        load(sourceLocation, properties);
        return properties;
    }

    public static void store(Properties source, File target) throws IOException {
        target.createNewFile(); // 确保文件存在
        // 先加载文件中的原数据，再写入
        Properties properties = new KeySortedProperties();
        load(target, properties);
        properties.putAll(source);
        OutputStream out = new FileOutputStream(target);
        properties.store(out, null);
        out.close();
    }

    public static void store(Properties source, String targetLocation) throws IOException {
        File file;
        if (targetLocation.startsWith(IOUtil.JAR_FILE_URL_PREFIX)) {
            file = new UrlResource(targetLocation).getFile();
        } else {
            file = ResourceUtils.getFile(targetLocation);
        }
        store(source, file);
    }

}
