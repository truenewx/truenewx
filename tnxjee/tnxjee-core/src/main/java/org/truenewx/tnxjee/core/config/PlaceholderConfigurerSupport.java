package org.truenewx.tnxjee.core.config;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 占位符配置支持。由于占位符配置器在配置属性生效前创建，所以本类下不能使用配置属性
 */
public abstract class PlaceholderConfigurerSupport {

    private static final String JAR_FILE_PREFIX = "jar:file:";

    @Bean
    public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Resource[] locations = getLocations();
        if (ArrayUtils.isNotEmpty(locations)) {
            configurer.setLocations(locations);
            configurer.setIgnoreResourceNotFound(true);
            configurer.setLocalOverride(true);
        }
        return configurer;
    }

    protected Resource[] getLocations() {
        Resource location = getLocation();
        if (location != null) {
            return new Resource[]{ location };
        }
        return null;
    }

    protected Resource getLocation() {
        try {
            Resource resource = new ClassPathResource(Strings.SLASH);

            String path = null;
            String url = resource.getURL().toString();
            if (url.startsWith(JAR_FILE_PREFIX)) { // 位于jar/war中
                int index = url.indexOf("ar!/");
                if (index > 0) {
                    path = url.substring(JAR_FILE_PREFIX.length(), index);
                    path = path.substring(0, path.lastIndexOf(Strings.SLASH) + 1); // 确保以/结尾
                }
            } else {
                File file = resource.getFile().getParentFile().getParentFile().getParentFile();
                if ("webapps".equals(file.getName())) { // 位于tomcat中
                    path = file.getParentFile().getAbsolutePath() + "/conf/";
                }
            }

            if (path != null) {
                path += getBasename() + ".properties";
                resource = new FileSystemResource(path);
                if (resource.exists()) {
                    System.out.println("====== Placeholder property source found: " + resource.getURL());
                    return resource;
                }
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
        return null;
    }

    /**
     * @return 不含扩展名的配置文件基础名称，需确保在配置目录中唯一
     */
    protected abstract String getBasename();

}
