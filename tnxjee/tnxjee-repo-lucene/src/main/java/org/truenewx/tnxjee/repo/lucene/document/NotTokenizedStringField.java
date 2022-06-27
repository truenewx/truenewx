package org.truenewx.tnxjee.repo.lucene.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

/**
 * 不分词的字符串字段，支持指定是否存储、是否参与排序，要分词的字段请使用{@link org.apache.lucene.document.TextField}
 *
 * @author jianglei
 */
public class NotTokenizedStringField extends Field {

    public static final FieldType TYPE_STORED = new FieldType();
    public static final FieldType TYPE_NOT_STORED = new FieldType();

    static {
        TYPE_STORED.setStored(true);
        TYPE_STORED.setTokenized(false);
        TYPE_STORED.setDocValuesType(DocValuesType.SORTED);
        TYPE_STORED.setOmitNorms(true);
        TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        TYPE_STORED.freeze();

        TYPE_NOT_STORED.setStored(false);
        TYPE_NOT_STORED.setTokenized(false);
        TYPE_NOT_STORED.setDocValuesType(DocValuesType.SORTED);
        TYPE_NOT_STORED.setOmitNorms(true);
        TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        TYPE_NOT_STORED.freeze();

    }

    public NotTokenizedStringField(String name, CharSequence value, boolean stored, boolean sorted) {
        super(name, new BytesRef(value), getType(stored, sorted));
    }

    public static IndexableFieldType getType(boolean stored, boolean sorted) {
        if (sorted) {
            return stored ? TYPE_STORED : TYPE_NOT_STORED;
        } else {
            return stored ? StringField.TYPE_STORED : StringField.TYPE_NOT_STORED;
        }
    }

}
