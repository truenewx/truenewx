package org.truenewx.tnxjee.model.validation.rule;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 标识规则
 *
 * @author jianglei
 */
public class MarkRule extends ValidationRule {

    private Set<Class<? extends Annotation>> annotationTypes = new HashSet<>();

    public MarkRule(Class<? extends Annotation> annotationType) {
        this.annotationTypes.add(annotationType);
    }

    public Collection<Class<? extends Annotation>> getAnnotationTypes() {
        return this.annotationTypes;
    }

    @Override
    public boolean isValid() {
        return this.annotationTypes.size() > 0;
    }

}
