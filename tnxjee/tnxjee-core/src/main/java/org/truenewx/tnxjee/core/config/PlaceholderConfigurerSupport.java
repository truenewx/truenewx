package org.truenewx.tnxjee.core.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * 占位符配置支持。由于占位符配置器在配置属性生效前创建，所以本类下不能使用配置属性
 */
public abstract class PlaceholderConfigurerSupport {

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
            Resource resource = new ClassPathResource("/");
            File root = resource.getFile().getParentFile().getParentFile().getParentFile().getParentFile();
            String path = root.getAbsolutePath() + "/conf/" + getAppName() + ".properties";
            return new FileSystemResource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return 当前应用的名称
     */
    protected abstract String getAppName();

}
