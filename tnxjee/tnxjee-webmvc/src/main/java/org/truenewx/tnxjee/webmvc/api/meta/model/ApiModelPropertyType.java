package org.truenewx.tnxjee.webmvc.api.meta.model;

import org.truenewx.tnxjee.core.caption.Caption;

/**
 * API模型属性类型，决定了输入方式，如：特殊的键盘布局
 */
public enum ApiModelPropertyType {

    @Caption("普通文本")
    TEXT,

    @Caption("Email地址")
    EMAIL,

    @Caption("Url地址")
    URL,

    @Caption("整数")
    INTEGER,

    @Caption("小数")
    DECIMAL,

    @Caption("开关")
    BOOLEAN,

    @Caption("日期")
    DATE,

    @Caption("时间")
    TIME,

    @Caption("日期时间")
    DATETIME,

    @Caption("选项")
    OPTION;

}
