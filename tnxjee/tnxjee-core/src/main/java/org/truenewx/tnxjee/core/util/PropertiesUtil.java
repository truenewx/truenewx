package org.truenewx.tnxjee.core.util;

import java.io.*;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

/**
 * 属性集工具类
 */
public class PropertiesUtil {

    private PropertiesUtil() {
    }

    public static void load(File source, Properties target) throws IOException {
        if (source.exists()) {
            InputStream in = new FileInputStream(source);
            target.load(in);
            in.close();
        }
    }

    public static void load(Resource source, Properties target) throws IOException {
        if (source.exists()) {
            InputStream in = source.getInputStream();
            target.load(in);
            in.close();
        }
    }

    public static void load(String sourceLocation, Properties target) throws IOException {
        if (sourceLocation.startsWith(IOUtil.JAR_FILE_URL_PREFIX)) {
            load(new UrlResource(sourceLocation), target);
        } else {
            File file = ResourceUtils.getFile(sourceLocation);
            load(file, target);
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
        Properties properties = new Properties();
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
