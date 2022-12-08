package org.truenewx.tnxjee.core.io;

import java.io.IOException;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ApplicationUtil;

/**
 * 基于应用根目录的资源样板加载器
 *
 * @author jianglei
 */
public class ApplicationRootResourcePatternLoader extends PathMatchingResourcePatternResolver {

    public ApplicationRootResourcePatternLoader() {
        super(new FileSystemResourceLoader());
    }

    @Override
    public Resource getResource(String location) {
        String absoluteLocation = ApplicationUtil.getAbsolutePath(location);
        if (!absoluteLocation.equals(location)) {
            location = Strings.SLASH + absoluteLocation;
        }
        return super.getResource(location);
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        String absoluteLocationPattern = ApplicationUtil.getAbsolutePath(locationPattern);
        if (!absoluteLocationPattern.equals(locationPattern)) {
            locationPattern = Strings.SLASH + absoluteLocationPattern;
        }
        return super.getResources(locationPattern);
    }

}
