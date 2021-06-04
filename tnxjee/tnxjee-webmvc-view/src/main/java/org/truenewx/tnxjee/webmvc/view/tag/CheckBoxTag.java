package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.view.tagext.ItemTagSupport;

/**
 * 复选框标签
 *
 * @author jianglei
 */
public class CheckBoxTag extends ItemTagSupport {

    public void setValue(String[] value) {
        super.setValue(value);
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
            return ArrayUtils.contains(values, itemValue);
        }
        return result;
    }

    @Override
    protected void resolveItem(String value, String text) throws IOException {
        print("<input type=\"checkbox\"");
        String id = getId();
        if (StringUtils.isNotBlank(id)) {
            print(Strings.SPACE, "id=\"", id, Strings.UNDERLINE, value, "\"");
        }
        print(Strings.SPACE, "value=\"", value, "\"");
        print(joinAttributes("id", "value"));
        if (isCurrentValue(value)) {
            print(" checked=\"checked\"");
        }
        print("/> ", text, Strings.ENTER);
    }

}
