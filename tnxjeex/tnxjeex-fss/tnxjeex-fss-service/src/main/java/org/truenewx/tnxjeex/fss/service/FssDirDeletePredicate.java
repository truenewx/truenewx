package org.truenewx.tnxjeex.fss.service;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public interface FssDirDeletePredicate {

    /**
     * 判断指定存储目录在包含有指定子目录清单和文件清单的情况下可否删除。默认当子目录清单和文件清单均为空时可删除。
     *
     * @param relativeDir 存储相对目录
     * @param subDirs     子目录清单
     * @param filenames   文件清单
     * @return 指定存储目录在包含有指定子目录清单和文件清单的情况下可否删除。
     */
    default boolean isDirDeletable(String relativeDir, List<String> subDirs, List<String> filenames) {
        return CollectionUtils.isEmpty(subDirs) && CollectionUtils.isEmpty(filenames);
    }

}
