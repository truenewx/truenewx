package org.truenewx.tnxjee.core.io;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * 基于Web根目录的上下文资源加载器
 *
 * @author jianglei
 * 
 */
public class WebContextResourceLoader extends DefaultResourceLoader
        implements ContextResourceLoader {

    private static final String WEBCONTEXT_URL_PREFIX = "webcontext:";

    @Override
    public ContextResource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(WEBCONTEXT_URL_PREFIX)) {
            return new WebContextResource(location.substring(WEBCONTEXT_URL_PREFIX.length()));
        }
        final Resource resource = super.getResource(location);
        if (resource instanceof ContextResource) {
            return (ContextResource) resource;
        } else if (resource instanceof ClassPathResource) {
            final ClassPathResource cpr = (ClassPathResource) resource;
            return new ClassPathContextResource(cpr.getPath(), cpr.getClassLoader());
        } else if (resource instanceof FileSystemResource) {
            final FileSystemResource fsr = (FileSystemResource) resource;
            return new FileSystemContextResource(fsr.getPath());
        }
        return null;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new WebContextResource(path);
    }

}
