package org.truenewx.tnxjee.core.version;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 抽象的版本号读取器
 *
 * @author jianglei
 */
public abstract class AbstractVersionReader implements VersionReader, ApplicationContextAware {

    private Version version;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        String fullVersion = readFullVersion(context);
        this.version = new Version(fullVersion);
    }

    @Override
    public Version getVersion() {
        return this.version;
    }

    /**
     * 读取完整版本号字符串
     *
     * @param context Spring容器上下文
     * @return 完整版本号字符串
     */
    protected abstract String readFullVersion(ApplicationContext context);


}
