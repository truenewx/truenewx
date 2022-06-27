package org.truenewx.tnxjee.repo.lucene.document;

import java.time.temporal.Temporal;

import org.truenewx.tnxjee.core.util.TemporalUtil;

/**
 * 时间索引字段
 *
 * @author jianglei
 */
public class TemporalField extends NotTokenizedStringField {

    public TemporalField(String name, Temporal value, boolean stored, boolean sorted) {
        super(name, TemporalUtil.format(value), stored, sorted);
    }

}
