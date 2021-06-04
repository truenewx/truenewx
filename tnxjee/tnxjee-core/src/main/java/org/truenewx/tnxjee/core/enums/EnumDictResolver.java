package org.truenewx.tnxjee.core.enums;

import java.util.Locale;

import org.truenewx.tnxjee.core.i18n.TextResolver;

/**
 * 枚举字典解析器
 *
 * @author jianglei
 */
public interface EnumDictResolver extends TextResolver {
    /**
     * 布尔枚举类型的简称
     */
    String BOOLEAN_ENUM_TYPE = "boolean";

    EnumDict getEnumDict(Locale locale);

    EnumType getEnumType(String type, Locale locale);

    /**
     * 获取指定类型和子类型确定的枚举子类型<br/>
     * 如果无法找到则尝试构建，如果构建也失败，则返回null
     *
     * @param type    枚举类型
     * @param subtype 枚举子类型
     * @param locale  地区
     * @return 指定类型和子类型确定的顶级枚举项
     */
    EnumType getEnumType(String type, String subtype, Locale locale);

    EnumItem getEnumItem(Enum<?> enumConstant, Locale locale);

    <E extends Enum<E>> E getEnumConstantByCaption(Class<E> enumClass, String caption,
            String groupCaption, Locale locale);

}
