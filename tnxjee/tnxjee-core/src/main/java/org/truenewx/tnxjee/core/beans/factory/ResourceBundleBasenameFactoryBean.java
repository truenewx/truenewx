package org.truenewx.tnxjee.core.beans.factory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.io.WebContextResource;
import org.truenewx.tnxjee.core.util.FileExtensions;

/**
 * 资源文件基本名称工厂Bean
 *
 * @author jianglei
 */
public class ResourceBundleBasenameFactoryBean implements FactoryBean<String[]> {

    private String[] basenames = new String[0];
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public void setBasename(String basename) throws IOException {
        setBasenames(basename.split(","));
    }

    public void setBasenames(String... basenames) throws IOException {
        if (basenames != null) {
            Set<String> basenameSet = new HashSet<>();
            for (String basename : basenames) {
                basename = basename.trim();
                Assert.hasText(basename, "Basename must not be empty");
                basename = basename.replace('\\', '/');
                boolean classpathBased = basename.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX);
                // 把basename中classpath:替换为classpath*:后进行查找
                String searchBasename = basename.replace(ResourceUtils.CLASSPATH_URL_PREFIX,
                        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX);
                // 查找文件资源
                Resource[] resources = this.resourcePatternResolver
                        .getResources(searchBasename + Strings.ASTERISK + FileExtensions.DOT_PROPERTIES);
                String jarStr = ".jar!/";
                for (Resource resource : resources) {
                    URL url = resource.getURL();
                    String absolutePath = url.getPath();
                    int jarIndex = absolutePath.indexOf(jarStr);
                    // 生成资源相对路径
                    String relativePath;
                    // 判断资源是否在jar包里
                    if (jarIndex > 0) {
                        relativePath = absolutePath.substring(jarIndex + jarStr.length());
                    } else {
                        Resource classPathResource;
                        if (classpathBased) { // 以classpath:开头的以源文件根目录为根检索
                            classPathResource = new ClassPathResource(Strings.SLASH);
                        } else { // 否则以WEB工程根目录为根检索
                            classPathResource = new WebContextResource(Strings.SLASH);
                        }
                        File rootDir = classPathResource.getFile();
                        relativePath = absolutePath.substring(rootDir.getPath().length() + 1)
                                .replace('\\', '/');
                    }
                    // 生成资源路径
                    StringBuffer resourcePath = new StringBuffer();
                    String fileName = FilenameUtils.getBaseName(resource.getFilename());
                    String[] fileNameArray = fileName.split(Strings.UNDERLINE);
                    String relativeDir = FilenameUtils.getFullPath(relativePath);
                    if (fileNameArray.length == 1) {
                        resourcePath.append(relativeDir).append(fileNameArray[0]);
                    } else if (fileNameArray.length == 2) {
                        resourcePath.append(relativeDir).append(fileNameArray[0]);
                    } else {
                        for (int i = 0; i < fileNameArray.length - 2; i++) {
                            resourcePath.append(relativeDir).append(fileNameArray[i]);
                        }
                    }
                    // 以classpath:加上该前缀classpath:
                    if (classpathBased) {
                        resourcePath.insert(0, ResourceUtils.CLASSPATH_URL_PREFIX);
                    }
                    basenameSet.add(resourcePath.toString());
                }

            }
            this.basenames = basenameSet.toArray(new String[basenameSet.size()]);
        } else {
            this.basenames = new String[0];
        }
    }

    @Override
    public String[] getObject() throws Exception {
        return this.basenames;
    }

    @Override
    public Class<?> getObjectType() {
        return String[].class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
