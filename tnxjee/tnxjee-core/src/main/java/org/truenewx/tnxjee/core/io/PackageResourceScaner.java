package org.truenewx.tnxjee.core.io;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 包资源扫描器，扫描指定包及其子包，查找满足指定文件名样式的文件资源
 *
 * @author jianglei
 * 
 */
public class PackageResourceScaner {

    public static final PackageResourceScaner INSTANCE = new PackageResourceScaner();

    private static final String PATH_PATTERN = "/**/";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private PackageResourceScaner() {
    }

    public List<Resource> scan(String[] packages, String[] fileNamePatterns) {
        List<Resource> list = new ArrayList<>();
        if (packages != null && fileNamePatterns != null) {
            for (String pkg : packages) {
                for (String fileNamePattern : fileNamePatterns) {
                    try {
                        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                                + ClassUtils.convertClassNameToResourcePath(pkg) + PATH_PATTERN
                                + fileNamePattern;
                        Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                        for (Resource resource : resources) {
                            list.add(resource);
                        }
                    } catch (Throwable e) {
                        LogUtil.warn(getClass(), e);
                    }
                }
            }
        }
        return list;
    }

}
