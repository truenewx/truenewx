package org.truenewx.tnxjee.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 文本文件内容转换器
 *
 * @author jianglei
 * 
 */
public class TextFileContentConverter implements FileContentConverter {

    private TextContentConverter textContentConverter;
    private ResourcePatternResolver resourcePatternResolver;

    public void setTextContentConverter(TextContentConverter textContentConverter) {
        this.textContentConverter = textContentConverter;
    }

    @Autowired
    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public void convert(String locationPattern, String encoding) {
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(locationPattern);
            if (resources.length == 0) {
                throw new FileNotFoundException(locationPattern);
            }
            for (Resource resource : resources) {
                File file = resource.getFile();
                FileInputStream in = new FileInputStream(file);
                String content = IOUtils.toString(in, encoding);
                in.close();

                content = this.textContentConverter.convert(content);

                FileOutputStream out = new FileOutputStream(file);
                IOUtils.write(content, out, encoding);
                out.close();
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
