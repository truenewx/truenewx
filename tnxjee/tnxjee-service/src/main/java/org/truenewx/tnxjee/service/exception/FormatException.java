package org.truenewx.tnxjee.service.exception;

import java.lang.annotation.Annotation;
import java.util.Objects;

import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;

/**
 * 格式异常，必须绑定属性
 *
 * @author jianglei
 */
public class FormatException extends SingleException {

    private static final long serialVersionUID = -7599751978935457915L;

    private Class<?> modelClass;

    public FormatException(String code, Class<?> modelClass, String property) {
        super(code);
        this.code = code;
        this.modelClass = modelClass;
        this.property = property;
    }

    public <A extends Annotation> FormatException(Class<A> constraintAnnotationType, Class<?> modelClass,
            String property) {
        this(constraintAnnotationType.getName() + ".message", modelClass, property);
    }

    public FormatException(ExceptionError error) {
        super(error);
    }

    public Class<?> getModelClass() {
        return this.modelClass;
    }

    @Override
    public boolean matches(String property) {
        if (super.matches(property)) {
            return true;
        }
        String simplePropertyPath = ClassUtil.getSimplePropertyPath(this.modelClass, property);
        if (simplePropertyPath.equals(property)) {
            return true;
        }
        String fullPropertyPath = ClassUtil.getFullPropertyPath(this.modelClass, property);
        return fullPropertyPath.equals(property);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.modelClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FormatException other = (FormatException) obj;
        return super.equals(other) && Objects.equals(this.modelClass, other.modelClass);
    }

}
