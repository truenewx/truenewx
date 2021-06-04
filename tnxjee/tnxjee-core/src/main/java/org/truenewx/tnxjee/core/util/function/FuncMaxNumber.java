package org.truenewx.tnxjee.core.util.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 函数：获取最大数值
 *
 * @author jianglei
 * 
 */
public class FuncMaxNumber implements Function<Class<?>, Number> {

    public static FuncMaxNumber INSTANCE = new FuncMaxNumber();

    private Map<Class<?>, Number> values = new HashMap<>();

    private FuncMaxNumber() {
        this.values.put(long.class, Long.MAX_VALUE);
        this.values.put(int.class, Integer.MAX_VALUE);
        this.values.put(short.class, Short.MAX_VALUE);
        this.values.put(byte.class, Byte.MAX_VALUE);
        this.values.put(double.class, Double.MAX_VALUE);
        this.values.put(float.class, Float.MAX_VALUE);
        this.values.put(Long.class, Long.MAX_VALUE);
        this.values.put(Integer.class, Integer.MAX_VALUE);
        this.values.put(Short.class, Short.MAX_VALUE);
        this.values.put(Byte.class, Byte.MAX_VALUE);
        this.values.put(Double.class, Double.MAX_VALUE);
        this.values.put(Float.class, Float.MAX_VALUE);
        this.values.put(BigDecimal.class, BigDecimal.valueOf(Double.MAX_VALUE));
        this.values.put(BigInteger.class, BigInteger.valueOf(Long.MAX_VALUE));
        this.values.put(Float.class, Float.MAX_VALUE);
    }

    @Override
    public Number apply(Class<?> type) {
        return this.values.get(type);
    }

}
