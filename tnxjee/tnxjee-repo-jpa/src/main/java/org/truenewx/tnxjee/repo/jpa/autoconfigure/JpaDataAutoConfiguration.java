package org.truenewx.tnxjee.repo.jpa.autoconfigure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.*;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.repo.jpa.hibernate.HibernateJpaPersistenceProvider;
import org.truenewx.tnxjee.repo.jpa.hibernate.MetadataProvider;
import org.truenewx.tnxjee.repo.jpa.init.DataSourceInitializeListener;
import org.truenewx.tnxjee.repo.jpa.init.ListenableSqlDataSourceScriptDatabaseInitializer;
import org.truenewx.tnxjee.repo.jpa.support.JpaAccessTemplate;

/**
 * JPA数据层自动配置
 *
 * @author jianglei
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
public class JpaDataAutoConfiguration extends JpaBaseConfiguration {

    private ApplicationContext context;
    private HibernateProperties hibernateProperties;

    // 在多数据源场景下，可创建子类，构造函数中指定DataSourceProperties和DataSource的beanName
    public JpaDataAutoConfiguration(
            DataSource dataSource,
            JpaProperties jpaProperties,
            ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider,
            ApplicationContext context,
            HibernateProperties hibernateProperties,
            SqlInitializationProperties sqlInitializationProperties,
            ObjectProvider<DataSourceInitializeListener> listenerProvider,
            ObjectProvider<Flyway> flywayProvider) {
        super(dataSource, jpaProperties, jtaTransactionManagerProvider);
        this.context = context;
        this.hibernateProperties = hibernateProperties;
        // 为了保证在flyway执行之前执行初始化脚本，在此显式执行数据库初始化，且执行后关闭后续框架的再次执行初始化
        // 根本目的在于将原本位于flyway之后的数据库初始化提前至flyway之前执行
        ListenableSqlDataSourceScriptDatabaseInitializer initializer = new ListenableSqlDataSourceScriptDatabaseInitializer(
                dataSource, sqlInitializationProperties, listenerProvider);
        initializer.initializeDatabase();
        sqlInitializationProperties.setMode(DatabaseInitializationMode.NEVER);
        // 在flyway执行之前，删除失败的增量脚本执行记录
        flywayProvider.ifAvailable(Flyway::repair);
    }

    protected String getSchema() {
        return null;
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        return this.hibernateProperties.determineHibernateProperties(getProperties().getProperties(),
                new HibernateSettings());
    }

    @Bean
    public HibernateJpaPersistenceProvider persistenceProvider() {
        return new HibernateJpaPersistenceProvider();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder,
            HibernateJpaPersistenceProvider persistenceProvider) {
        addMappingResources(getProperties().getMappingResources());
        LocalContainerEntityManagerFactoryBean factoryBean = super.entityManagerFactory(factoryBuilder);
        factoryBean.setPersistenceProvider(persistenceProvider);
        return factoryBean;
    }

    protected void addMappingResources(List<String> mappingResources) {
        List<String> adding = new ArrayList<>();
        Iterator<String> iterator = mappingResources.iterator();
        while (iterator.hasNext()) {
            String location = iterator.next();
            int wildcardIndex = location.indexOf(Strings.ASTERISK);
            if (wildcardIndex > 0) { // 不支持处理以*开头的路径
                try {
                    String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + location;
                    Resource[] resources = this.context.getResources(pattern);
                    for (Resource resource : resources) {
                        String path = resource.getURI().toString();
                        String dir = location.substring(0, wildcardIndex);
                        adding.add(path.substring(path.lastIndexOf(dir)));
                    }
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
                iterator.remove();
            }
        }
        mappingResources.addAll(adding);
    }

    @Bean
    public JpaAccessTemplate jpaAccessTemplate(EntityManagerFactory factory, MetadataProvider metadataProvider) {
        String schema = getSchema();
        if (schema == null) {
            return new JpaAccessTemplate(factory, metadataProvider);
        } else {
            return new JpaAccessTemplate(schema, factory, metadataProvider);
        }
    }

}
