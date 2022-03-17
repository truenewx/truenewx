package org.truenewx.tnxjee.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.SpringUtil;

/**
 * 基于配置子目录的环境配置后置处理器，于Spring容器启动前加载基于分层机制的自定义配置属性
 *
 * @author jianglei
 */
public class ConfigSubDirEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DEFAULT_ROOT_LOCATION = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "config";
    private static final String SOURCE_NAME_PREFIX = Framework.NAME + "Config: ";
    private static final String BASENAME = "application"; // 配置文件基本名称固定为application，以便于IDE工具编辑

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
    private PropertiesPropertySourceLoader propertiesLoader = new PropertiesPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            MutablePropertySources propertySources = environment.getPropertySources();
            String rootLocation = getRootLocation(environment);
            // 先从根目录中加载配置
            boolean added = addPropertySource(environment, rootLocation);
            // 找出根目录下的所有子目录并按照优先级排序
            List<String> dirNames = getSortedDirNames(rootLocation);
            // 从顶层至底层依次加载配置文件以确保上层配置优先
            for (String dirName : dirNames) {
                String dirLocation = rootLocation + Strings.SLASH + dirName;
                added = addPropertySource(environment, dirLocation) || added;
            }

            if (added) {
                System.out.println("====== Config Sub Dir Property Sources ======");
                for (PropertySource<?> propertySource : propertySources) {
                    if (propertySource.getName().startsWith(SOURCE_NAME_PREFIX)) {
                        System.out.println(propertySource.getName());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getRootLocation(ConfigurableEnvironment environment) {
        return environment.getProperty("spring.config.location", DEFAULT_ROOT_LOCATION);
    }

    protected List<String> getSortedDirNames(String rootLocation) throws IOException {
        List<String> list = new ArrayList<>();
        // 模式/*无法找出位于jar包中的目录，故通过模式/*/application*先找出配置文件，再取得所处目录
        Resource[] resources = this.resourcePatternResolver.getResources(
                rootLocation + "/*/" + BASENAME + Strings.ASTERISK);
        for (Resource resource : resources) {
            String url = resource.getURL().toString();
            url = url.substring(0, url.lastIndexOf(Strings.SLASH));
            String dirName = url.substring(url.lastIndexOf(Strings.SLASH) + 1);
            if (!list.contains(dirName)) {
                list.add(dirName);
            }
        }
        list.sort((dirName1, dirName2) -> {
            int ordinal1 = getDirOrdinal(dirName1);
            int ordinal2 = getDirOrdinal(dirName2);
            int result = Integer.compare(ordinal1, ordinal2);
            if (result == 0) {
                // 子目录序号相同，则比较子目录名称，由于core,model,repo,service,web的层级名称符合自然排序，所以此处只需简单比较名称即可
                result = dirName1.compareTo(dirName2);
            }
            return result;
        });
        return list;
    }

    private int getDirOrdinal(String dirName) {
        // tnxjee 优先于 tnxjeex 优先于 应用
        if (dirName.startsWith(Framework.NAME + Strings.MINUS)) {
            return 0;
        }
        if (dirName.startsWith(Framework.NAME + "x" + Strings.MINUS)) {
            return 1;
        }
        return 2;
    }

    private boolean addPropertySource(ConfigurableEnvironment environment, String dirLocation) throws IOException {
        List<Resource> list = new ArrayList<>();
        String location = dirLocation + Strings.SLASH + BASENAME;
        String profile = SpringUtil.getActiveProfile(environment);
        if (StringUtils.isNotBlank(profile)) {
            // [dir]/application-[profile].*
            addResources(list, location + Strings.MINUS + profile + Strings.DOT + Strings.ASTERISK);
        }
        // [dir]/[basename].*
        addResources(list, location + Strings.DOT + Strings.ASTERISK);
        list.sort((res1, res2) -> {
            String filename1 = res1.getFilename();
            String filename2 = res2.getFilename();
            String extension1 = FilenameUtils.getExtension(filename1);
            String extension2 = FilenameUtils.getExtension(filename2);
            int result = Integer.compare(getExtensionOrdinal(extension1), getExtensionOrdinal(extension2));
            if (result == 0) { // 扩展名序号相同，则简单比较文件名即可，带profile的自然靠前
                return filename1.compareTo(filename2);
            }
            return result;
        });
        boolean added = false;
        MutablePropertySources propertySources = environment.getPropertySources();
        for (Resource resource : list) {
            added = addPropertySource(propertySources, dirLocation, resource) || added;
        }
        return added;
    }

    private void addResources(List<Resource> validList, String locationPattern) throws IOException {
        Resource[] resources = this.resourcePatternResolver.getResources(locationPattern);
        for (Resource resource : resources) {
            String extension = FilenameUtils.getExtension(resource.getFilename());
            if (getExtensionOrdinal(extension) >= 0) {
                validList.add(resource);
            }
        }
    }

    private boolean addPropertySource(MutablePropertySources propertySources, String dirLocation, Resource resource)
            throws IOException {
        if (resource.exists()) {
            String filename = resource.getFilename();
            PropertySourceLoader sourceLoader = getSourceLoader(filename);
            if (sourceLoader != null) {
                String sourceName = SOURCE_NAME_PREFIX + Strings.LEFT_SQUARE_BRACKET + dirLocation + Strings.SLASH
                        + filename + Strings.RIGHT_SQUARE_BRACKET;
                if (!propertySources.contains(sourceName)) {
                    PropertySource<?> propertySource = CollectionUtil
                            .getFirst(sourceLoader.load(sourceName, resource), null);
                    if (propertySource != null) {
                        propertySources.addLast(propertySource);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private PropertySourceLoader getSourceLoader(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (getExtensionOrdinal(extension) > 0) {
            return this.yamlLoader;
        } else if (getExtensionOrdinal(extension) == 0) {
            return this.propertiesLoader;
        }
        return null;
    }

    private int getExtensionOrdinal(String extension) {
        if (extension != null) {
            switch (extension) {
                case FileExtensions.PROPERTIES:
                    return 0;
                case FileExtensions.YAML:
                    return 1;
                case FileExtensions.YML:
                    return 2;
                default:
                    break;
            }
        }
        return -1;
    }
}
