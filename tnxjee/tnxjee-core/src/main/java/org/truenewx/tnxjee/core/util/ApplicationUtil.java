package org.truenewx.tnxjee.core.util;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 基于当前框架的应用的工具类
 *
 * @author jianglei
 */
public class ApplicationUtil {

    public static final String JAR_FILE_URL_PREFIX = ResourceUtils.JAR_URL_PREFIX + ResourceUtils.FILE_URL_PREFIX;
    public static final String JAR_WORKING_DIR_SUFFIX = "ar!";

    private ApplicationUtil() {
    }

    public static boolean isInJar(String path) {
        return path.startsWith(JAR_FILE_URL_PREFIX) || path.endsWith(JAR_WORKING_DIR_SUFFIX);
    }

    public static boolean isInJar() {
        return isInJar(getWorkingDirLocation());
    }

    public static boolean isInTomcat() {
        return isInTomcat(getWorkingDirLocation());
    }

    private static boolean isInTomcat(String dirLocation) {
        return webappsIndexOf(dirLocation) >= 0;
    }

    private static int webappsIndexOf(String path) {
        return path.replace('\\', '/').indexOf("/webapps/");
    }

    public static String getTomcatRootLocation(String dirLocation) {
        int index = webappsIndexOf(dirLocation);
        if (index >= 0) {
            return dirLocation.substring(0, index);
        }
        return null;
    }

    public static String getWorkingDirLocation() {
        Resource resource = new ClassPathResource(Strings.SLASH);
        try {
            String url = resource.getURL().toString();
            if (isInJar(url)) { // 位于jar/war中
                int index = url.indexOf(JAR_WORKING_DIR_SUFFIX); // 一定有
                return url.substring(JAR_FILE_URL_PREFIX.length(),
                        index + JAR_WORKING_DIR_SUFFIX.length()); // 以.jar!或.war!结尾
            } else {
                return resource.getFile().getParentFile().getParentFile().getAbsolutePath();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取工作临时目录，与工作目录相关，非系统临时目录
     *
     * @return 工作临时目录
     */
    public static File getWorkingTempDir() {
        String rootLocation = getWorkingDirLocation();
        if (rootLocation != null) {
            String tomcatRootLocation = getTomcatRootLocation(rootLocation);
            if (tomcatRootLocation != null) {
                rootLocation = tomcatRootLocation;
            }
            if (rootLocation.endsWith(JAR_WORKING_DIR_SUFFIX)) {
                int index = rootLocation.lastIndexOf(Strings.SLASH);
                rootLocation = rootLocation.substring(0, index);
            }
            return new File(rootLocation + "/temp");
        }
        return null;
    }

    /**
     * @return 应用根目录定位路径
     */
    public static String getApplicationRootLocation() {
        String location = getWorkingDirLocation(); // 默认情况下，根目录为工作目录
        String tomcatRootLocation = getTomcatRootLocation(location);
        if (tomcatRootLocation != null) { // 位于tomcat中，则根目录为tomcat安装目录
            location = tomcatRootLocation;
        } else if (location.endsWith(JAR_WORKING_DIR_SUFFIX)) { // 位于jar中，则根目录为上级目录的上级目录
            location = location.substring(0, location.lastIndexOf(Strings.SLASH));
            location = location.substring(0, location.lastIndexOf(Strings.SLASH));
        }
        return location;
    }
}
