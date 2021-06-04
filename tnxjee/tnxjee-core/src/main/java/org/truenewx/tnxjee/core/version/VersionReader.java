package org.truenewx.tnxjee.core.version;

/**
 * 版本号读取器
 *
 * @author jianglei
 */
public interface VersionReader {

    /**
     * 获取当前版本号
     *
     * @return 当前版本号
     */
    Version getVersion();

}
