package org.truenewx.tnxjee.repo.validation.rule.builder;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjee.model.validation.constraint.Cellphone;
import org.truenewx.tnxjee.model.validation.constraint.IdCardNo;
import org.truenewx.tnxjee.model.validation.rule.RegexRule;

/**
 * 正则表达式规则构建器
 *
 * @author jianglei
 */
@Component
public class RegexRuleBuilder implements ValidationRuleBuilder<RegexRule> {
    /**
     * 默认消息
     */
    public static final String DEFAULT_MESSAGE = StringUtils.join("{", Pattern.class.getName(), ".message}");

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[]{ Pattern.class, Cellphone.class, IdCardNo.class, };
    }

    @Override
    public RegexRule create(Annotation annotation) {
        Binate<String, String> binate = getRegexpMessage(annotation);
        if (binate != null) {
            return new RegexRule(binate.getLeft(), binate.getRight());
        }
        return null;
    }

    private Binate<String, String> getRegexpMessage(Annotation annotation) {
        Pattern pattern = null;
        String message;
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType == Pattern.class) {
            pattern = (Pattern) annotation;
            message = pattern.message();
        } else {
            pattern = annotationType.getAnnotation(Pattern.class);
            if (annotationType == Cellphone.class) {
                message = ((Cellphone) annotation).message();
            } else if (annotationType == IdCardNo.class) {
                message = ((IdCardNo) annotation).message();
            } else {
                message = DEFAULT_MESSAGE;
            }
        }
        if (pattern != null) {
            return new Binary<>(pattern.regexp(), message);
        }
        return null;
    }

    @Override
    public void update(Annotation annotation, RegexRule rule) {
        Binate<String, String> binate = getRegexpMessage(annotation);
        if (binate != null) {
            String regexp = binate.getLeft();
            if (StringUtils.isNotBlank(regexp)) {
                rule.setExpression(regexp);
            }
            String message = binate.getRight();
            if (!DEFAULT_MESSAGE.equals(message)) {
                rule.setMessage(message);
            }
        }
    }

}
