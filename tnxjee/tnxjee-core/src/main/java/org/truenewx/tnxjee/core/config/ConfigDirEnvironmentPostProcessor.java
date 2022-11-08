package org.truenewx.tnxjee.core.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
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

    private static final String PROPERTY_SOURCE_NAME_PREFIX = Framework.NAME + " property source: ";
    private static final String DIR_NAME = "config";
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

            // 再添加内部配置
            added = addInternalPropertySources(environment, added);

            if (added) {
                String profile = SpringUtil.getActiveProfile(environment);
                if (Profiles.LOCAL.equals(profile)
                        || environment.getProperty(PROPERTY_ENV_CONFIG_PRINT, Boolean.class, Boolean.FALSE)) {
                    System.out.println("====== Classpath Config Dir Property Sources ======");
                    MutablePropertySources propertySources = environment.getPropertySources();
                    for (PropertySource<?> propertySource : propertySources) {
                        if (propertySource.getName().startsWith(PROPERTY_SOURCE_NAME_PREFIX)) {
                            System.out.println(propertySource.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean addExternalPropertySources(ConfigurableEnvironment environment) throws IOException {
        String dirLocation = getExternalConfigDirLocation();
        File dir = new File(dirLocation);
        if (dir.exists()) {
            String appName = SpringUtil.getApplicationName(environment);
            if (appName == null) {
                System.out.println(
                        "====== Can't load property 'spring.application.name', please make sure it is in classpath:application.properties/yaml/yml");
            } else {
                dirLocation = ResourceUtils.FILE_URL_PREFIX + dirLocation;
                // 先添加应用特有配置属性
                boolean added = addPropertySources(environment, dirLocation, appName);
                // 再添加与公共配置属性（因为是外部配置，所以存在与其它应用公用配置的可能性）
                added = addPropertySources(environment, dirLocation, BASENAME) || added;
                return added;
            }
        }
        return false;
    }

    public static String getExternalConfigDirLocation() throws IOException {
        String rootLocation = IOUtil.getWorkingDirLocation();
        String tomcatRootLocation = IOUtil.getTomcatRootLocation(rootLocation);
        if (tomcatRootLocation != null) { // 位于tomcat中，则根目录为tomcat安装目录
            rootLocation = tomcatRootLocation;
        } else if (rootLocation.endsWith(IOUtil.JAR_WORKING_DIR_SUFFIX)) { // 位于jar中，则根目录为上级目录的上级目录
            rootLocation = rootLocation.substring(0, rootLocation.lastIndexOf(Strings.SLASH));
            rootLocation = rootLocation.substring(0, rootLocation.lastIndexOf(Strings.SLASH));
        }
        return rootLocation + "/conf"; // 外部配置目录为conf，与内部配置目录名不同
    }

    private boolean addInternalPropertySources(ConfigurableEnvironment environment, boolean added) throws IOException {
        String rootLocation = getInternalRootLocation(environment);
        // 找出根目录下的所有子目录并按照优先级排序
        List<String> dirNames = getSortedDirNames(rootLocation);
        // 从顶层至底层依次加载配置文件以确保上层配置优先
        for (String dirName : dirNames) {
            String dirLocation = rootLocation + Strings.SLASH + dirName;
            added = addPropertySources(environment, dirLocation, BASENAME) || added;
        }
        // 最后从根目录中加载最低优先级的配置
        added = addPropertySources(environment, rootLocation, BASENAME) || added;
        return added;
    }

    private String getInternalRootLocation(ConfigurableEnvironment environment) {
        // 内部配置目录为config，与spring默认的配置目录保持一致
        return environment.getProperty("spring.config.location",
                ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "config");
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
        if (ResourceUtils.FILE_URL_PREFIX.equals(dirLocation)) { // 基于文件系统绝对路径
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
                String sourceName = PROPERTY_SOURCE_NAME_PREFIX + resource.getURL();
                if (!propertySources.contains(sourceName)) {
                    PropertySource<?> propertySource = CollectionUtil.getFirst(sourceLoader.load(sourceName, resource),
                            null);
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
