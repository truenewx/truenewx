package org.truenewx.tnxjee.repo.lucene.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.truenewx.tnxjee.repo.lucene.index.IndexWriterFactory;
import org.truenewx.tnxjee.repo.lucene.store.DirectoryFactory;
import org.truenewx.tnxjee.repo.lucene.store.FsDirectoryFactory;
import org.truenewx.tnxjee.repo.lucene.store.RamDirectoryFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

@Configuration
public class LuceneConfig {

    @Bean
    @ConditionalOnMissingBean(DirectoryFactory.class)
    public DirectoryFactory directoryFactory(Environment environment) {
        String root = environment.getProperty("tnxjee.repo.index.dir");
        if (StringUtils.isNotBlank(root)) {
            return new FsDirectoryFactory(root);
        }
        // 默认使用内存中的存储目录工厂
        return new RamDirectoryFactory();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(Analyzer.class)
    public Analyzer analyzer() {
        return new IKAnalyzer(true);
    }

    @Bean
    @ConditionalOnMissingBean(IndexWriterFactory.class)
    public IndexWriterFactory indexWriterFactory(DirectoryFactory directoryFactory, Analyzer analyzer) {
        return new IndexWriterFactory(directoryFactory, analyzer);
    }

}
