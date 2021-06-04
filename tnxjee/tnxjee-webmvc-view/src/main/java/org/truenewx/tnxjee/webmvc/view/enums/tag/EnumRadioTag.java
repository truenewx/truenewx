package org.truenewx.tnxjee.webmvc.view.enums.tag;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.webmvc.view.enums.tagext.EnumItemTagSupport;

/**
 * 基于枚举的单选框标签
 *
 * @author jianglei
 */
public class EnumRadioTag extends EnumItemTagSupport {

    @Override
    protected String getName() {
        String name = super.getName();
        if (StringUtils.isBlank(name)) {
            name = getId();
            if (StringUtils.isBlank(name)) {
                if (StringUtils.isBlank(this.subtype)) {
                    name = this.type;
                } else {
                    name = StringUtils.join(this.type, Strings.UNDERLINE, this.subtype);
                }
            }
        }
        return name;
    }

    @Override
    protected void resolveItem(String value, String text) throws IOException {
        print("<input type=\"radio\"");
        String name = getName();
        if (StringUtils.isNotBlank(name)) {
            print(" name=\"", name, "\"");
        }
        String id = getId();
        if (StringUtils.isNotBlank(id)) {
            print(Strings.SPACE, id, Strings.UNDERLINE, value);
        }
        print(" value=\"", value, "\"");
        print(joinAttributes("id", "name", "value"));
        if (isCurrentValue(value)) {
            print(" checked=\"checked\"");
        }
        print("> ", text, Strings.ENTER);
    }

    @Override
    protected boolean isCurrentValue(String itemValue) {
        // 如果不允许空选项，但当前值为空，则将第一个选项的值视为当前值
        if (!this.emptyItem) {
            String value = this.value == null ? Strings.EMPTY : this.value.toString();
            if (StringUtils.isEmpty(value)) {
                Object firstItem = CollectionUtil.get(getItems(), 0);
                if (firstItem != null) {
                    return getItemValue(firstItem).equals(itemValue);
                }
            }
        }
        return super.isCurrentValue(itemValue);
    }

}
