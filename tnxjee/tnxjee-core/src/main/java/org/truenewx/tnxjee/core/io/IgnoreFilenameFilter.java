package org.truenewx.tnxjee.core.io;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 忽略文件名的文件过滤器
 *
 * @author jianglei
 * 
 */
public class IgnoreFilenameFilter implements IOFileFilter {

    private String[] ignoredPatterns;

    public IgnoreFilenameFilter(String ignoredPattern) {
        setIgnoredPattern(ignoredPattern);
    }

    public void setIgnoredPattern(String ignoredPattern) {
        this.ignoredPatterns = ignoredPattern.split(",");
    }

    @Override
    public boolean accept(File dir, String name) {
        String path = dir.getAbsolutePath() + Strings.SLASH + name;
        return !StringUtil.wildcardMatchOneOf(path, this.ignoredPatterns);
    }

    @Override
    public boolean accept(File dir) {
        String path = dir.getAbsolutePath();
        return !StringUtil.wildcardMatchOneOf(path, this.ignoredPatterns);
    }

}
