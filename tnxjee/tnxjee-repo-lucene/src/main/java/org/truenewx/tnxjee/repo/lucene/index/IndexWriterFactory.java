package org.truenewx.tnxjee.repo.lucene.index;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.truenewx.tnxjee.repo.lucene.store.DirectoryFactory;

/**
 * 索引写入器工厂
 *
 * @author jianglei
 */
public class IndexWriterFactory {

    private DirectoryFactory directoryFactory;
    private Analyzer analyzer;

    public IndexWriterFactory(DirectoryFactory directoryFactory, Analyzer analyzer) {
        this.directoryFactory = directoryFactory;
        this.analyzer = analyzer;
    }

    public IndexWriter getIndexWriter(Class<?> indexedClass) throws IOException {
        Directory directory = this.directoryFactory.getDirectory(indexedClass);
        IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
        prepareConfig(config);
        return new IndexWriter(directory, config);
    }

    protected void prepareConfig(IndexWriterConfig config) {
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }

}
