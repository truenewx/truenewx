package org.truenewx.tnxjee.core.spec;

import java.util.Map;

/**
 * 属性动态的
 *
 * @author jianglei
 */
public interface PropertyDynamic {

    /**
     * 设置属性值
     *
     * @param name  属性名
     * @param value 属性值
     */
    void set(String name, Object value);

    /**
     * 获取属性值
     *
     * @param name 属性名
     * @param <T>  属性类型
     * @return 属性值
     */
    <T> T get(String name);

    /**
     * 设置所有属性，这会清除原有的所有属性
     *
     * @param map 所有属性-值映射集
     */
    void setAll(Map<String, ?> map);

    /**
     * 获取所有属性-值映射集，修改该映射集对当前对象无影响
     *
     * @return 名值映射集
     */
    Map<String, Object> getAll();

    /**
     * 将指定的旧名称的值切换到新名称
     *
     * @param oldName 旧名称
     * @param newName 新名称
     * @param retain  是否保留旧值
     */
    void shift(String oldName, String newName, boolean retain);

}
