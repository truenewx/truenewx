package org.truenewx.tnxjee.repo.lucene.support;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.Paged;
import org.truenewx.tnxjee.model.query.QueryResult;
import org.truenewx.tnxjee.repo.index.IndexRepo;
import org.truenewx.tnxjee.repo.lucene.document.IndexFieldFeature;
import org.truenewx.tnxjee.repo.lucene.document.NotTokenizedStringField;
import org.truenewx.tnxjee.repo.lucene.document.TemporalField;
import org.truenewx.tnxjee.repo.lucene.index.IndexWriterFactory;
import org.truenewx.tnxjee.repo.lucene.search.DefaultQueryBuilder;
import org.truenewx.tnxjee.repo.lucene.search.DefaultSortBuilder;

/**
 * 基于Lucene的索引数据仓库支持
 *
 * @param <T> 被索引对象类型
 * @author jianglei
 */
public abstract class LuceneIndexRepoSupport<T> implements IndexRepo<T>, DisposableBean {

    private IndexWriterFactory writerFactory;
    private IndexWriter writer;
    private QueryParser queryParser;
    private IndexSearcher searcher;
    private Map<String, Class<?>> propertyTypes;

    @Autowired
    public void setWriterFactory(IndexWriterFactory writerFactory) throws IOException {
        this.writerFactory = writerFactory;
        applyWriter();
        applyQueryParser();
        this.propertyTypes = ClassUtil.getPropertyTypes(getIndexedClass());
    }

    protected void applyWriter() throws IOException {
        Class<T> indexedClass = getIndexedClass();
        this.writer = this.writerFactory.getIndexWriter(indexedClass);
    }

    protected void applyQueryParser() {
        this.queryParser = new QueryParser(getDefaultPropertyName(), getAnalyzer());
        this.queryParser.setDefaultOperator(QueryParser.Operator.AND);
    }

    /**
     * 获取索引检索器
     *
     * @return 索引检索器，如果当前索引不可检索，则返回null
     */
    protected final IndexSearcher getSearcher() {
        if (this.searcher == null) {
            try {
                // 必须用索引目录对象创建读取器，否则无法查到数据
                Directory directory = this.writer.getDirectory();
                if (DirectoryReader.indexExists(directory)) {
                    IndexReader reader = DirectoryReader.open(directory);
                    this.searcher = new IndexSearcher(reader);
                }
            } catch (IOException e) {
                LogUtil.error(getClass(), e);
            }
        }
        return this.searcher;
    }

    protected Class<T> getIndexedClass() {
        return ClassUtil.getActualGenericType(getClass(), IndexRepo.class, 0);
    }

    protected final Analyzer getAnalyzer() {
        return this.writer.getAnalyzer();
    }

    @Override
    public void destroy() throws Exception {
        if (this.searcher != null) {
            this.searcher.getIndexReader().close();
        }
        this.writer.close();
    }

    /**
     * 获取标识属性名
     *
     * @return 标识属性名，默认为"id"
     */
    protected String getKeyPropertyName() {
        return "id";
    }

    /**
     * 获取默认的索引属性名称。被索引对象必然有至少一个索引属性，否则整个对象都无需索引
     *
     * @return 默认的索引属性名称
     */
    protected abstract String getDefaultPropertyName();

    @Override
    public void save(T object) {
        Document document = toDocument(object);
        try {
            if (document != null && document.iterator().hasNext()) {
                this.writer.addDocument(document);
            }
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    /**
     * 被索引对象转换为索引文档。<br>
     * 注意：必须包含标识属性同名索引字段，否则无法正确执行删除操作<br>
     * 提示：精确等于比对的字段添加{@link StringField}，关键字匹配的字段添加{@link TextField}
     *
     * @param object             被索引对象
     * @param excludedProperties 排除的属性名称集
     * @return 索引文档
     */
    protected Document toDocument(T object, String... excludedProperties) {
        Document document = new Document();
        BeanUtil.loopProperties(object, (name, value) -> {
            if (value != null) {
                if (name.equals(getKeyPropertyName())) {
                    document.add(getField(name, value, true, false, false));
                    document.add(getField(name, value, false, false, false));
                } else {
                    getFields(name, value).forEach(document::add);
                }
            }
        }, excludedProperties);
        return document;
    }

    /**
     * 获取指定属性的不存储的索引字段信息集
     *
     * @param name  索引字段名，不含标识属性
     * @param value 索引字段值
     * @return 索引字段信息，返回null表示当前条件下没有对应的索引字段
     */
    protected Collection<IndexableField> getFields(String name, Object value) {
        Collection<IndexableField> fields = new ArrayList<>();
        if (value != null) {
            IndexFieldFeature feature = getFieldFeature(name);
            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    Object element = Array.get(value, i);
                    IndexableField field = getField(name, element, feature.isStored(), feature.isTokenized(),
                            feature.isSorted());
                    if (field != null) {
                        fields.add(field);
                    }
                }
            } else if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                for (Object element : collection) {
                    IndexableField field = getField(name, element, feature.isStored(), feature.isTokenized(),
                            feature.isSorted());
                    if (field != null) {
                        fields.add(field);
                    }
                }
            } else {
                IndexableField field = getField(name, value, feature.isStored(), feature.isTokenized(),
                        feature.isSorted());
                if (field != null) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    /**
     * 获取指定属性的索引字段特性
     *
     * @param name 属性名
     * @return 索引字段特性，不能返回null，默认：不存储、不分词、不参与排序
     */
    @NotNull
    protected IndexFieldFeature getFieldFeature(String name) {
        if (name.equals(getDefaultPropertyName())) { // 默认索引字段不存储、分词、不参与排序
            return new IndexFieldFeature(false, true, false);
        }
        return new IndexFieldFeature();
    }

    /**
     * 获取指定属性的唯一索引字段信息<br>
     * 如果一个属性对应多个索引字段，请子类覆写 {@link #getFields(String, Object)}
     *
     * @param name      索引字段名，不含标识属性
     * @param value     索引字段值
     * @param stored    是否存储
     * @param tokenized 是否分词，仅对字符串索引的字段有效
     * @param sorted    是否参与排序
     * @return 索引字段信息，返回null表示指定属性没有对应的索引字段
     */
    protected IndexableField getField(String name, Object value, boolean stored, boolean tokenized, boolean sorted) {
        if (value == null || ClassUtil.isComplex(value.getClass())) {
            return null;
        }
        if (value instanceof Number) {
            if (value instanceof Long) {
                if (sorted) {
                    return new NumericDocValuesField(name, (Long) value);
                } else if (stored) {
                    return new StoredField(name, (Long) value);
                } else {
                    return new LongPoint(name, (Long) value);
                }
            }
            if (value instanceof Integer) {
                if (sorted) {
                    return new NumericDocValuesField(name, (Integer) value);
                } else if (stored) {
                    return new StoredField(name, (Integer) value);
                } else {
                    return new IntPoint(name, (Integer) value);
                }
            }
            if (value instanceof BigDecimal) {
                double doubleValue = ((BigDecimal) value).doubleValue();
                if (sorted) {
                    return new DoubleDocValuesField(name, doubleValue);
                } else if (stored) {
                    return new StoredField(name, doubleValue);
                } else {
                    return new DoublePoint(name, doubleValue);
                }
            }
            if (value instanceof Double) {
                if (sorted) {
                    return new DoubleDocValuesField(name, (Double) value);
                } else if (stored) {
                    return new StoredField(name, (Double) value);
                } else {
                    return new DoublePoint(name, (Double) value);
                }
            }
            if (value instanceof Float) {
                if (sorted) {
                    return new FloatDocValuesField(name, (Float) value);
                } else if (stored) {
                    return new StoredField(name, (Float) value);
                } else {
                    return new FloatPoint(name, (Float) value);
                }
            }
        }
        if (value instanceof Temporal) {
            return new TemporalField(name, (Temporal) value, stored, sorted);
        }
        // 除以上类型外，字符串和枚举类型的字段不可分词
        if (value instanceof Boolean || value instanceof Enum) {
            tokenized = false;
        }
        if (tokenized) {
            return new TextField(name, value.toString(), stored ? Field.Store.YES : Field.Store.NO);
        }
        return new NotTokenizedStringField(name, value.toString(), stored, sorted);
    }

    /**
     * 获取指定属性的不存储的唯一索引字段信息<br>
     * 如果一个属性对应多个索引字段，请子类覆写 {@link #getFields(String, Object)}
     *
     * @param name  索引字段名，不含标识属性
     * @param value 索引字段值
     * @return 索引字段信息，返回null表示指定属性没有对应的索引字段
     */
    protected IndexableField getField(String name, Object value) {
        // 数字、时间、枚举类型默认参与排序
        boolean sorted = value instanceof Number || value instanceof Temporal || value instanceof Enum;
        return getField(name, value, false, false, sorted);
    }

    @Override
    public void delete(T object) {
        String propertyName = getKeyPropertyName();
        Object propertyValue = BeanUtil.getPropertyValue(object, propertyName);
        Query query = DefaultQueryBuilder.create(propertyName, propertyValue);
        try {
            this.writer.deleteDocuments(query);
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    /**
     * 判断当前索引可否查询
     *
     * @return 当前索引可否查询
     */
    @Override
    public boolean isQueryable() {
        try {
            return DirectoryReader.indexExists(this.writer.getDirectory());
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
            return false;
        }
    }

    protected final Query parse(String ql) {
        try {
            // 逻辑运算符大写化，以符合Lucene查询语句规范
            ql = ql.replaceAll(" and ", " AND ").replaceAll(" or ", " OR ");
            return this.queryParser.parse(ql);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected final Query parse(CharSequence ql, Map<String, Object> params) {
        String s = ql.toString();
        if (params != null && params.size() > 0) {
            String[] follows = { Strings.SPACE, Strings.SINGLE_QUOTES, Strings.DOUBLE_QUOTES, "\\)" };
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    // 去掉参数值中的空格，避免条件字段错误
                    value = value.toString().replaceAll(Strings.SPACE, Strings.EMPTY);
                    String name = entry.getKey();
                    String key = Strings.COLON + name;
                    for (String follow : follows) {
                        s = s.replaceAll(key + follow, value + follow);
                    }
                    if (s.endsWith(key)) {
                        s = s.substring(0, s.length() - key.length()) + value;
                    }
                }
            }
        }
        return parse(s);
    }

    /**
     * 分页查询
     *
     * @param query        查询条件
     * @param pageSize     分页大小
     * @param pageNo       页码
     * @param sort         排序
     * @param fieldsToLoad 查询结果要加载的字段集，为空时不加载任何字段
     * @return 索引文档分页查询结果
     */
    protected QueryResult<Document> query(Query query, int pageSize, int pageNo, Sort sort, String... fieldsToLoad) {
        try {
            List<Document> documents = new ArrayList<>();
            long total = 0;
            IndexSearcher searcher = getSearcher();
            if (searcher != null) {
                ScoreDoc afterDoc = getAfterDoc(query, pageSize, pageNo, sort);
                int n = pageSize > 0 ? pageSize : Integer.MAX_VALUE;
                TopDocs topDocs = searcher.searchAfter(afterDoc, query, n, sort);
                total = topDocs.totalHits.value;
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document document = searcher.doc(scoreDoc.doc, ArrayUtil.toSet(fieldsToLoad));
                    documents.add(document);
                }
            }
            return new QueryResult<>(documents, new Paged(pageSize, pageNo, total));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ScoreDoc getAfterDoc(Query query, int pageSize, int pageNo, Sort sort) throws IOException {
        int n = pageSize * (pageNo - 1);
        if (n > 0) {
            TopDocs topDocs = this.searcher.search(query, n, sort);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            return scoreDocs[scoreDocs.length - 1];
        }
        return null;
    }

    /**
     * 构建索引排序
     *
     * @param orders 字段排序集
     * @return 索引排序
     */
    protected Sort buildSort(List<FieldOrder> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        }
        DefaultSortBuilder builder = new DefaultSortBuilder();
        for (FieldOrder order : orders) {
            String fieldName = order.getName();
            Class<?> fieldType = this.propertyTypes.get(fieldName);
            SortField sortField = buildSortField(fieldName, fieldType, order.isDesc());
            builder.add(sortField);
        }
        return builder.build();
    }

    /**
     * 构建索引排序字段
     *
     * @param fieldName 排序字段名
     * @param fieldType 推断的字段类型，无法推断时为null
     * @param desc      是否倒序
     * @return 索引排序字段
     */
    protected SortField buildSortField(String fieldName, Class<?> fieldType, boolean desc) {
        SortField.Type sortFieldType = getSortFieldType(fieldName, fieldType);
        return new SortField(fieldName, sortFieldType, desc);
    }

    /**
     * 获取排序字段类型
     *
     * @param fieldName 排序字段名
     * @param fieldType 推断的字段类型，无法推断时为null
     * @return 排序字段类型
     */
    protected SortField.Type getSortFieldType(String fieldName, Class<?> fieldType) {
        IndexFieldFeature feature = getFieldFeature(fieldName);
        if (fieldType != null) { // 数字类型一定可排序
            if (fieldType == Long.class || fieldType == long.class) {
                return SortField.Type.LONG;
            }
            if (fieldType == Integer.class || fieldType == int.class) {
                return SortField.Type.INT;
            }
            if (fieldType == BigDecimal.class || fieldType == Double.class || fieldType == double.class) {
                return SortField.Type.DOUBLE;
            }
            if (fieldType == Float.class || fieldType == float.class) {
                return SortField.Type.FLOAT;
            }
        }
        if (feature.isSorted()) {
            // 分词字段 -> 计分排序
            if (feature.isTokenized()) {
                return SortField.Type.SCORE;
            }
            // 默认使用字符串顺序
            return SortField.Type.STRING;
        }
        // 不参与排序则使用文档顺序
        return SortField.Type.DOC;
    }

    @Override
    public void commit() {
        try {
            this.writer.forceMergeDeletes();
            this.writer.commit();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    @Override
    public void rollback() {
        try {
            this.writer.rollback();
            // 回滚后writer会关闭，需重新创建
            applyWriter();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
