package org.truenewx.tnxjee.service.exception;

/**
 * 可解决的异常<br>
 * 仅作为标识，在进行异常处理时便于判断
 *
 * @author jianglei
 */
public abstract class ResolvableException extends RuntimeException {

    private static final long serialVersionUID = -4552090901512143756L;

    private Object payload;

    public ResolvableException() {
        super();
    }

    public ResolvableException(String message) {
        super(message);
    }

    /**
     * 设置负载，用于在异常处理过程中向后续处理环节传递数据
     *
     * @param payload 负载
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayload() {
        return (T) this.payload;
    }

}
