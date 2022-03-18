package org.truenewx.tnxjee.core.util.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 函数：获取最小数值
 *
 * @author jianglei
 */
public class FuncMinNumber implements Function<Class<?>, Number> {

    public static FuncMinNumber INSTANCE = new FuncMinNumber();

    private Map<Class<?>, Number> values = new HashMap<>();

    private FuncMinNumber() {
        this.values.put(long.class, Long.MIN_VALUE);
        this.values.put(int.class, Integer.MIN_VALUE);
        this.values.put(short.class, Short.MIN_VALUE);
        this.values.put(byte.class, Byte.MIN_VALUE);
        this.values.put(double.class, Double.MIN_VALUE);
        this.values.put(float.class, Float.MIN_VALUE);
        this.values.put(Long.class, Long.MIN_VALUE);
        this.values.put(Integer.class, Integer.MIN_VALUE);
        this.values.put(Short.class, Short.MIN_VALUE);
        this.values.put(Byte.class, Byte.MIN_VALUE);
        this.values.put(Double.class, Double.MIN_VALUE);
        this.values.put(Float.class, Float.MIN_VALUE);
        this.values.put(BigDecimal.class, BigDecimal.valueOf(Double.MIN_VALUE));
        this.values.put(BigInteger.class, BigInteger.valueOf(Long.MIN_VALUE));
    }

    @Override
    public Number apply(Class<?> type) {
        return this.values.get(type);
    }

}
