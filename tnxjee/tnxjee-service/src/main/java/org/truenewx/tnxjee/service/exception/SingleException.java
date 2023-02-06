package org.truenewx.tnxjee.service.exception;

import java.util.Objects;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;

/**
 * 只包含一个异常信息的单异常<br>
 * 仅作为标识，在进行异常处理时便于判断
 *
 * @author jianglei
 */
public abstract class SingleException extends ResolvableException {

    private static final long serialVersionUID = -7817976044788876682L;

    protected String code;
    protected String property;

    protected SingleException(String message) {
        super(message);
    }

    protected SingleException(ExceptionError error) {
        this(error.getMessage());
        this.code = error.getCode();
        this.property = error.getField();
    }

    public String getCode() {
        return this.code;
    }

    public String getProperty() {
        return this.property;
    }

    /**
     * 判断异常错误消息是否已经过本地化处理，经过本地化处理后方可呈现给用户查看
     *
     * @return 异常错误消息是否已经过本地化处理
     */
    public abstract boolean isMessageLocalized();

    public boolean matches(String property) {
        if (this.property == null) { // 未绑定属性，则指定匹配空属性
            return property == null;
        } else { // 已绑定属性，则匹配*、相等的属性
            return Strings.ASTERISK.equals(property) || this.property.equals(property);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.property);
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
        return Objects.deepEquals(this.code, other.code)
                && Objects.deepEquals(this.property, other.property);
    }

}
