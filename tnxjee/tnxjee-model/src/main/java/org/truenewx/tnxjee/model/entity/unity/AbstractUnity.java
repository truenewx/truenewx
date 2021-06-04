package org.truenewx.tnxjee.model.entity.unity;

import java.io.Serializable;

/**
 * 抽象单体
 *
 * @param <K> 标识的类型
 * @author jianglei
 */
@Deprecated // JPA框架无法识别子类实体的id字段
public abstract class AbstractUnity<K extends Serializable> implements Unity<K> {
    /**
     * 标识
     */
    private K id;

    /**
     * 用指定标识构造单体对象
     *
     * @param id 标识
     */
    protected AbstractUnity(K id) {
        this.id = id;
    }

    /**
     * 默认构造函数
     */
    public AbstractUnity() {
    }

    @Override
    public K getId() {
        return this.id;
    }

    protected void setId(K id) {
        this.id = id;
    }

}
