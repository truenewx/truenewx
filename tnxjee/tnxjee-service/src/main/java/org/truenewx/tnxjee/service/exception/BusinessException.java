package org.truenewx.tnxjee.service.exception;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;

/**
 * 业务异常，可以绑定属性，默认未绑定属性
 *
 * @author jianglei
 */
public class BusinessException extends SingleException {

    private static final long serialVersionUID = 3188183601455385859L;

    private Object[] args;

    public BusinessException(String code, Object... args) {
        super(toMessage(code, args));
        this.code = code;
        this.args = args;
    }

    private static String toMessage(String code, Object... args) {
        String message = code;
        if (ArrayUtils.isNotEmpty(args)) {
            message += Strings.LEFT_SQUARE_BRACKET
                    + StringUtils.join(args, ", ")
                    + Strings.RIGHT_SQUARE_BRACKET;
        }
        return message;
    }

    public BusinessException(ExceptionError error) {
        super(error);
    }

    public Object[] getArgs() {
        return this.args;
    }

    /**
     * 与指定属性绑定
     *
     * @param property 绑定的属性
     * @return 当前异常对象自身
     */
    public BusinessException bind(String property) {
        this.property = property;
        return this;
    }

    /**
     * 判断是否已绑定属性
     *
     * @return 是否已绑定属性
     */
    public boolean isBoundProperty() {
        return StringUtils.isNotBlank(this.property);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.args);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BusinessException other = (BusinessException) obj;
        return super.equals(other) && Objects.deepEquals(this.args, other.args);
    }

}
