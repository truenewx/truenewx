package org.truenewx.tnxjee.model.validation.rule;

/**
 * 字符串长度规则
 *
 * @author jianglei
 */
public class LengthRule extends RangeRule<Integer> {

    public LengthRule(int min, int max) {
        super(min, max);
    }

    public LengthRule() {
        this(0, Integer.MAX_VALUE);
    }

}
