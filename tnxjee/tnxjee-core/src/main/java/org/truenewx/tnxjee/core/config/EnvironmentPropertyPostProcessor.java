package org.truenewx.tnxjee.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.Layers;
import org.truenewx.tnxjee.core.util.SpringUtil;

/**
 * 配置属性提交处理器，用于在Spring容器启动前加载自定义的配置属性
 *
 * @author jianglei
 */
public class EnvironmentPropertyPostProcessor implements EnvironmentPostProcessor {

    private static final String DEFAULT_ROOT_LOCATION = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "config";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
    private PropertiesPropertySourceLoader propertiesLoader = new PropertiesPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            MutablePropertySources propertySources = environment.getPropertySources();
            String rootLocation = getRootLocation(environment);
            String profile = SpringUtil.getActiveProfile(environment);
            if (profile == null) { // 无法取得profile时，先从固定的profile.properties文件中加载profile配置，使默认profile生效
                String profileLocation = rootLocation + Strings.SLASH + "profile.properties";
                Resource profileResource = this.resourcePatternResolver.getResource(profileLocation);
                addPropertySource(propertySources, rootLocation, profileResource);
            }
            // 从顶层至底层依次加载配置文件以确保上层配置优先
            for (String basename : Layers.ALL_DESC) {
                addPropertySource(environment, rootLocation, basename);
            }

            System.out.println("====== Layer-based Property Sources ======");
            for (PropertySource<?> propertySource : propertySources) {
                if (propertySource instanceof OriginTrackedMapPropertySource) {
                    System.out.println(propertySource);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getRootLocation(ConfigurableEnvironment environment) {
        return environment.getProperty("spring.config.location", DEFAULT_ROOT_LOCATION);
    }

    private void addPropertySource(ConfigurableEnvironment environment, String rootLocation, String basename)
            throws IOException {
        List<Resource> list = new ArrayList<>();
        String location = rootLocation + Strings.SLASH + basename;
        String profile = SpringUtil.getActiveProfile(environment);
        if (StringUtils.isNotBlank(profile)) {
            // [root]/[basename]-[profile].*
            addResources(list, location + Strings.MINUS + profile + Strings.DOT + Strings.ASTERISK);
        }
        // [root]/[basename].*
        addResources(list, location + Strings.DOT + Strings.ASTERISK);
        list.sort((res1, res2) -> {
            String filename1 = res1.getFilename();
            String filename2 = res2.getFilename();
            String extension1 = FilenameUtils.getExtension(filename1);
            String extension2 = FilenameUtils.getExtension(filename2);
            int result = Integer.compare(getOrdinal(extension1), getOrdinal(extension2));
            if (result == 0) { // 扩展名序号相同，则简单比较文件名即可，带profile的自然靠前
                return filename1.compareTo(filename2);
            }
            return result;
        });
        MutablePropertySources propertySources = environment.getPropertySources();
        for (Resource resource : list) {
            addPropertySource(propertySources, rootLocation, resource);
        }
    }

    private void addResources(List<Resource> validList, String locationPattern) throws IOException {
        Resource[] resources = this.resourcePatternResolver.getResources(locationPattern);
        for (Resource resource : resources) {
            String extension = FilenameUtils.getExtension(resource.getFilename());
            if (getOrdinal(extension) >= 0) {
                validList.add(resource);
            }
        }
    }

    private void addPropertySource(MutablePropertySources propertySources, String rootLocation,
            Resource resource) throws IOException {
        if (resource.exists()) {
            String filename = resource.getFilename();
            PropertySourceLoader sourceLoader = getSourceLoader(filename);
            if (sourceLoader != null) {
                String sourceName = Framework.NAME + "LayerConfig: [" + rootLocation + Strings.SLASH + filename + "]";
                if (!propertySources.contains(sourceName)) {
                    PropertySource<?> propertySource = CollectionUtil
                            .getFirst(sourceLoader.load(sourceName, resource), null);
                    if (propertySource != null) {
                        propertySources.addLast(propertySource);
                    }
                }
            }
        }
    }

    private PropertySourceLoader getSourceLoader(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (getOrdinal(extension) > 0) {
            return this.yamlLoader;
        } else if (getOrdinal(extension) == 0) {
            return this.propertiesLoader;
        }
        return null;
    }

    private int getOrdinal(String extension) {
        if (extension != null) {
            switch (extension) {
                case "properties":
                    return 0;
                case "yaml":
                    return 1;
                case "yml":
                    return 2;
                default:
                    break;
            }
        }
        return -1;
    }
}
