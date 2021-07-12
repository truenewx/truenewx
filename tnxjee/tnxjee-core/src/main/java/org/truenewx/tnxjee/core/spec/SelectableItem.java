package org.truenewx.tnxjee.core.spec;

import java.io.Serializable;

import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 可选项
 */
public interface SelectableItem<V extends Serializable> {

    /**
     * @return 取值
     */
    V getValue();

    /**
     * @return 显示文本
     */
    String getText();

    /**
     * @return 查询索引，默认为文本的拼音
     */
    default String getIndex() {
        return StringUtil.toPinyin(getIndex());
    }

}
