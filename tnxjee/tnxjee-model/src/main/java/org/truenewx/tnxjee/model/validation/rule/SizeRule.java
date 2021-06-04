package org.truenewx.tnxjee.model.validation.rule;

/**
 * 集合大小规则
 *
 * @author jianglei
 */
public class SizeRule extends RangeRule<Integer> {

    public SizeRule(int min, int max) {
        super(min, max);
    }

    public SizeRule() {
        this(0, Integer.MAX_VALUE);
    }

}
