package org.truenewx.tnxjee.repo.jpa.init;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.core.io.Resource;

/**
 * 数据源初始化侦听器
 *
 * @author jianglei
 */
public interface DataSourceInitializeListener {

    default boolean isExecutable(DataSource dataSource, Resource script) {
        return true;
    }

    default void afterRun(List<Resource> scripts) {
    }

}
