package org.truenewx.tnxjee.repo.lucene.document;

import java.time.temporal.Temporal;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 时间索引字段
 *
 * @author jianglei
 */
public class TemporalField extends Field {

    public static final FieldType TYPE_STORED = new FieldType();
    public static final FieldType TYPE_NOT_STORED = new FieldType();

    static {
        TYPE_STORED.setStored(true);
        TYPE_STORED.setTokenized(false);
        TYPE_STORED.setDocValuesType(DocValuesType.SORTED);
        TYPE_STORED.freeze();

        TYPE_NOT_STORED.setTokenized(false);
        TYPE_NOT_STORED.setDocValuesType(DocValuesType.SORTED);
        TYPE_NOT_STORED.freeze();
    }

    public TemporalField(String name, Temporal value, boolean stored) {
        super(name, TemporalUtil.format(value), stored ? TYPE_STORED : TYPE_NOT_STORED);
    }

    public TemporalField(String name, Temporal value, Store stored) {
        this(name, value, stored == Store.YES);
    }

}
