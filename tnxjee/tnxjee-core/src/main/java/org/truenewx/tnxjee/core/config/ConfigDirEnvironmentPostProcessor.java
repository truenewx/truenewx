package org.truenewx.tnxjee.core.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.*;

/**
 * 基于配置目录的环境配置后置处理器，于Spring容器启动前加载基于分层机制的自定义配置属性
 *
 * @author jianglei
 */
public class ConfigDirEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME_PREFIX = Framework.NAME + " config resource: ";
    public static final String BASENAME = "application";
    private static final String PROPERTY_ENV_CONFIG_PRINT = "env.config.print";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
    private PropertiesPropertySourceLoader propertiesLoader = new PropertiesPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // 优先添加外部配置
            boolean added = addExternalPropertySources(environment);

            // 再添加层级配置
            added = addLayerPropertySources(environment, added);

            if (added && environment.getProperty(PROPERTY_ENV_CONFIG_PRINT, Boolean.class, Boolean.FALSE)) {
                System.out.println("====== Config Resources ======");
                MutablePropertySources propertySources = environment.getPropertySources();
                for (PropertySource<?> propertySource : propertySources) {
                    if (propertySource instanceof OriginTrackedMapPropertySource) {
                        System.out.println(propertySource.getName());
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    private boolean addExternalPropertySources(ConfigurableEnvironment environment) throws IOException {
        String dirLocation = getExternalConfigDirLocation();
        File dir = new File(dirLocation);
        if (dir.exists()) {
            String basename = ApplicationUtil.getSymbol(environment);
            if (StringUtils.isBlank(basename)) {
                basename = SpringUtil.getApplicationName(environment);
            }
            if (StringUtils.isBlank(basename)) {
                System.out.println("====== Can't load property '" + AppConstants.PROPERTY_FRAMEWORK_APP_SYMBOL
                        + "' and '" + AppConstants.PROPERTY_SPRING_APP_NAME
                        + "', please make sure one of them is in classpath:application.properties/yaml/yml");
            } else {
                dirLocation = ResourceUtils.FILE_URL_PREFIX + dirLocation;
                // 先添加应用特有配置属性
                boolean added = addPropertySources(environment, dirLocation, basename);
                // 再添加与公共配置属性（因为是外部配置，所以存在与其它应用公用配置的可能性）
                added = addPropertySources(environment, dirLocation, BASENAME) || added;
                return added;
            }
        }
        return false;
    }

    public static String getExternalConfigDirLocation() {
        return ApplicationUtil.getApplicationRootLocation() + "/conf"; // 外部配置目录为conf，与内部配置目录名不同
    }

    private boolean addLayerPropertySources(ConfigurableEnvironment environment, boolean added) throws IOException {
        String rootLocation = environment.getProperty("spring.config.location",
                ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "config");
        // 找出根目录下的所有子目录并按照优先级排序
        List<String> dirNames = getSortedLayerDirNames(rootLocation);
        // 从顶层至底层依次加载配置文件以确保上层配置优先
        for (String dirName : dirNames) {
            String dirLocation = rootLocation + Strings.SLASH + dirName;
            added = addPropertySources(environment, dirLocation, BASENAME) || added;
        }
        return added;
    }

    protected List<String> getSortedLayerDirNames(String rootLocation) throws IOException {
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
                // 子目录序号相同，则比较子目录名称
                // 由于core,model,repo,service,web的层级名称符合自然排序，所以此处只需简单倒序排列名称即可，确保更高层更靠前
                result = dirName2.compareTo(dirName1);
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

    private boolean addPropertySources(ConfigurableEnvironment environment, String dirLocation, String basename)
            throws IOException {
        List<Resource> list = new ArrayList<>();
        String profile = SpringUtil.getActiveProfile(environment);
        if (dirLocation.startsWith(ResourceUtils.FILE_URL_PREFIX)) { // 基于文件系统绝对路径
            dirLocation = dirLocation.substring(ResourceUtils.FILE_URL_PREFIX.length());
            if (StringUtils.isNotBlank(profile)) {
                // [dir]/application-[profile].*
                addFileSystemResources(list, dirLocation,
                        basename + Strings.MINUS + profile + Strings.DOT + Strings.ASTERISK);
            }
            // [dir]/[basename].*
            addFileSystemResources(list, dirLocation, basename + Strings.DOT + Strings.ASTERISK);
        } else { // 基于classpath
            String location = dirLocation + Strings.SLASH + basename;
            if (StringUtils.isNotBlank(profile)) {
                // [dir]/application-[profile].*
                addClasspathResources(list, location + Strings.MINUS + profile + Strings.DOT + Strings.ASTERISK);
            }
            // [dir]/[basename].*
            addClasspathResources(list, location + Strings.DOT + Strings.ASTERISK);
        }
        list.sort((res1, res2) -> {
            String filename1 = res1.getFilename();
            String filename2 = res2.getFilename();
            String extension1 = StringUtil.getExtension(filename1);
            String extension2 = StringUtil.getExtension(filename2);
            int result = Integer.compare(getExtensionOrdinal(extension1), getExtensionOrdinal(extension2));
            if (result == 0) { // 扩展名序号相同，则简单比较文件名即可，带profile的自然靠前
                return filename1.compareTo(filename2);
            }
            return result;
        });
        boolean added = false;
        MutablePropertySources propertySources = environment.getPropertySources();
        for (Resource resource : list) {
            added = addPropertySource(propertySources, resource) || added;
        }
        return added;
    }

    private void addFileSystemResources(List<Resource> validList, String dirLocation, String filenamePattern) {
        File dir = new File(dirLocation);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (StringUtil.wildcardMatch(file.getName(), filenamePattern)) {
                    validList.add(new FileSystemResource(file));
                }
            }
        }
    }

    private void addClasspathResources(List<Resource> validList, String locationPattern) throws IOException {
        Resource[] resources = this.resourcePatternResolver.getResources(locationPattern);
        for (Resource resource : resources) {
            String extension = StringUtil.getExtension(resource.getFilename());
            if (getExtensionOrdinal(extension) >= 0) {
                validList.add(resource);
            }
        }
    }

    private boolean addPropertySource(MutablePropertySources propertySources, Resource resource) throws IOException {
        if (resource.exists()) {
            String filename = resource.getFilename();
            PropertySourceLoader sourceLoader = getSourceLoader(filename);
            if (sourceLoader != null) {
                String sourceName = PROPERTY_SOURCE_NAME_PREFIX + resource.getDescription();
                if (!propertySources.contains(sourceName)) {
                    PropertySource<?> propertySource = CollectionUtil.getFirst(sourceLoader.load(sourceName, resource),
                            null);
                    if (propertySource != null) {
                        // 带-[profile]的配置文件插入到首个默认配置文件前
                        if (sourceName.contains(BASENAME + Strings.MINUS)) {
                            for (PropertySource<?> ps : propertySources) {
                                if (ps instanceof OriginTrackedMapPropertySource) {
                                    String name = ps.getName();
                                    if (!name.startsWith(PROPERTY_SOURCE_NAME_PREFIX)) {
                                        propertySources.addBefore(name, propertySource);
                                        return true;
                                    }
                                }
                            }
                        } else { // 不带-[profile]的配置文件插入到首个application.*默认配置文件前
                            for (PropertySource<?> ps : propertySources) {
                                if (ps instanceof OriginTrackedMapPropertySource) {
                                    String name = ps.getName();
                                    if (!name.startsWith(PROPERTY_SOURCE_NAME_PREFIX)
                                            && name.contains(BASENAME + Strings.DOT)) {
                                        propertySources.addBefore(name, propertySource);
                                        return true;
                                    }
                                }
                            }
                        }
                        // 没有找到插入位置，则加入到最后（最低优先级）
                        propertySources.addLast(propertySource);
                    }
                }
            }
        }
        return false;
    }

    private PropertySourceLoader getSourceLoader(String filename) {
        String extension = StringUtil.getExtension(filename);
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
