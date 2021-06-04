package org.truenewx.tnxjee.core.version;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Maven版本号读取器
 *
 * @author jianglei
 */
@Component
@PropertySource("classpath:maven.properties")
public class MavenVersionReader extends AbstractVersionReader {

    @Override
    protected String readFullVersion(ApplicationContext context) {
        return context.getEnvironment().getProperty("project.version");
    }

}
