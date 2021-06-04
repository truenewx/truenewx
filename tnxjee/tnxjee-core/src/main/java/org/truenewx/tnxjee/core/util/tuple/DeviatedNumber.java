package org.truenewx.tnxjee.core.util.tuple;

import java.io.Serializable;

import org.truenewx.tnxjee.core.spec.Deviation;

/**
 * 有偏差的数值
 *
 * @author jianglei
 * 
 */
public class DeviatedNumber<T extends Number> implements Serializable {

    private static final long serialVersionUID = 2187467700636462571L;

    private T value;
    private Deviation deviation;

    public DeviatedNumber(T value, Deviation deviation) {
        this.value = value;
        this.deviation = deviation;
    }

    public T getValue() {
        return this.value;
    }

    public Deviation getDeviation() {
        return this.deviation;
    }

}
