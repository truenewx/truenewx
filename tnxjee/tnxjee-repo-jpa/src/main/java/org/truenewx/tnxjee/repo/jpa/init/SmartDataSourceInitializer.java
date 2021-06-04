package org.truenewx.tnxjee.repo.jpa.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.Profiles;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;

/**
 * 智能数据源初始化器
 */
public abstract class SmartDataSourceInitializer {

    private static final String FILENAME_SCHEMA = "schema.sql";
    private static final String FILENAME_DATA = "data.sql";

    @Autowired
    private ProfileSupplier profileSupplier;
    @Autowired
    private ApplicationContext context;
    private String rootLocation = ResourcePatternResolver.CLASSPATH_URL_PREFIX + "sql";
    private Logger logger = LogUtil.getLogger(getClass());

    public void setRootLocation(String rootLocation) {
        this.rootLocation = rootLocation;
    }

    public boolean isDisabled() {
        return Profiles.JUNIT.equals(this.profileSupplier.get());
    }

    public void execute(DataSource dataSource) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        Map<String, List<Resource>> mapping = getVersionResourcesMapping(connection);
        if (mapping.size() > 0) {
            String lastVersion = null;
            Exception executeException = null;
            this.logger.info("======== Begin execute sql scripts:");
            for (Map.Entry<String, List<Resource>> entry : mapping.entrySet()) {
                try {
                    Resource[] resources = entry.getValue().toArray(new Resource[0]);
                    DatabasePopulator databasePopulator = new ResourceDatabasePopulator(resources);
                    databasePopulator.populate(connection);
                    lastVersion = entry.getKey();
                } catch (Exception e) {
                    // 有一个版本的脚本出现问题，则缓存异常，并中止后续版本的脚本执行
                    executeException = e;
                    break;
                }
            }
            this.logger.info("======== The above scripts are executed.");
            try {
                if (lastVersion != null) {
                    updateVersion(connection, lastVersion);
                    this.logger.info("======== The last executed script version is {}", lastVersion);
                }
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            } finally {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
            if (executeException != null) { // 如果有脚本执行异常，则抛出异常中止服务启动
                throw new RuntimeException(executeException);
            }
        } else {
            this.logger.info("======== No scripts need to be executed.");
        }
    }

    protected Map<String, List<Resource>> getVersionResourcesMapping(Connection connection) {
        try {
            Map<String, List<Resource>> mapping = new TreeMap<>();
            Resource rootResource = this.context.getResource(this.rootLocation);
            String rootPath = rootResource.getURI().toString();
            int rootPathLength = rootPath.length();
            Resource[] resources = this.context.getResources(this.rootLocation + "/*/*.sql");
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    String path = resource.getURI().toString();
                    String relativePath = path.substring(rootPathLength);
                    if (relativePath.startsWith(Strings.SLASH)) {
                        relativePath = relativePath.substring(1);
                    }
                    String version = relativePath.substring(0, relativePath.indexOf(Strings.SLASH));
                    if (executable(connection, version)) {
                        List<Resource> versionResources = mapping.computeIfAbsent(version, k -> new ArrayList<>());
                        String filename = resource.getFilename();
                        if (FILENAME_SCHEMA.equals(filename) || FILENAME_DATA.equals(filename)) {
                            versionResources.add(resource);
                            versionResources.sort(Comparator.comparing(this::getResourceOrdinal));
                        }
                    }
                }
            }
            return mapping;
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
            return Collections.emptyMap();
        }
    }

    private Integer getResourceOrdinal(Resource resource) {
        // schema优先于data
        return FILENAME_SCHEMA.equals(resource.getFilename()) ? 0 : 1;
    }

    protected abstract boolean executable(Connection connection, String version) throws SQLException;

    protected abstract void updateVersion(Connection connection, String version) throws SQLException;

}
