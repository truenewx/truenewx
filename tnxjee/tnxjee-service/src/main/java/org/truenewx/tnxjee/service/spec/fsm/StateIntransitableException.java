package org.truenewx.tnxjee.service.spec.fsm;


import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 状态不可进行的转换异常
 *
 * @author jianglei
 */
public class StateIntransitableException extends BusinessException {

    private static final long serialVersionUID = 3918018972788204325L;

    /**
     * 状态不可转换的异常错误码
     */
    public static String CODE = "error.service.fsm.state_intransitable";

    public StateIntransitableException(Enum<?> state, Enum<?> transition) {
        super(CODE, state, transition);
    }

}
