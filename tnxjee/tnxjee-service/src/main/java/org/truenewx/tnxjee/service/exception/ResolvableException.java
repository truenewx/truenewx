package org.truenewx.tnxjee.service.exception;

/**
 * 可解决的异常<br>
 * 仅作为标识，在进行异常处理时便于判断
 *
 * @author jianglei
 */
public abstract class ResolvableException extends RuntimeException {

    private static final long serialVersionUID = -4552090901512143756L;

    public ResolvableException() {
        super();
    }

    public ResolvableException(String message) {
        super(message);
    }

}
