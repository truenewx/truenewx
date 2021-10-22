package org.truenewx.tnxjee.model.validation.rule;

/**
 * 校验规则
 *
 * @author jianglei
 */
public abstract class ValidationRule {

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        // 一组校验规则中，同一种规则最多只能有一个
        return obj != null && getClass() == obj.getClass();
    }

    /**
     * 判断当前规则是否有效，无效规则将不会映射至前端
     *
     * @return 当前规则是否有效
     */
    public abstract boolean isValid();

}
