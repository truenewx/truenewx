package org.truenewx.tnxjee.webmvc.view.enums.tag;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.view.enums.tagext.EnumItemTagSupport;

/**
 * 基于枚举的复选框标签
 *
 * @author jianglei
 */
public class EnumCheckBoxTag extends EnumItemTagSupport {

    public void setValues(Enum<?>[] value) {
        String[] array = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = value[i].name();
        }
        super.setValue(array);
    }

    @Override
    protected boolean isCurrentValue(String itemValue) {
        boolean result = super.isCurrentValue(itemValue);
        if (!result) {
            String[] values;
            if (this.value instanceof String) {
                values = ((String) this.value).split(Strings.COMMA);
            } else if (this.value instanceof String[]) {
                values = (String[]) this.value;
            } else {
                return false;
            }
            if (itemValue != null) {
                itemValue = itemValue.toString();
            }
            return ArrayUtils.contains(values, itemValue);
        }
        return result;
    }

    @Override
    protected void resolveItem(String value, String text) throws IOException {
        print("<input type=\"checkbox\"");
        String id = getId();
        if (StringUtils.isNotBlank(id)) {
            print(" id=\"", id, Strings.UNDERLINE, value, "\"");
        }
        print(" value=\"", value, "\"");
        print(joinAttributes("id", "value"));
        if (isCurrentValue(value)) {
            print(" checked=\"checked\"");
        }
        print("/> ", text, Strings.ENTER);
    }

}
