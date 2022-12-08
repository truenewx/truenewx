package org.truenewx.tnxjee.repo.jpa.init;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.io.ApplicationRootResourcePatternLoader;

public class ListenableSqlDataSourceScriptDatabaseInitializer extends SqlDataSourceScriptDatabaseInitializer {

    private DataSourceInitializeListener listener;

    public ListenableSqlDataSourceScriptDatabaseInitializer(
            DataSource dataSource,
            SqlInitializationProperties properties,
            ObjectProvider<DataSourceInitializeListener> listenerProvider) {
        super(dataSource, properties);
        setResourceLoader(new ApplicationRootResourcePatternLoader());
        this.listener = listenerProvider.getIfAvailable();
    }

    @Override
    protected void runScripts(List<Resource> resources, boolean continueOnError, String separator, Charset encoding) {
        if (this.listener != null) {
            List<Resource> scripts = new ArrayList<>();
            for (Resource resource : resources) {
                if (resource.exists() && this.listener.isExecutable(getDataSource(), resource)) {
                    scripts.add(resource);
                }
            }
            resources = scripts;
        }
        if (resources.size() > 0) {
            super.runScripts(resources, continueOnError, separator, encoding);
            if (this.listener != null) {
                this.listener.afterRun(resources);
            }
        }
    }

}
