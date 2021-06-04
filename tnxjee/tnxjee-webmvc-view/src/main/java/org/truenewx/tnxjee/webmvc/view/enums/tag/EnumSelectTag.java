package org.truenewx.tnxjee.webmvc.view.enums.tag;

import java.io.IOException;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.view.enums.tagext.EnumItemTagSupport;

/**
 * 基于枚举的下拉框标签
 *
 * @author jianglei
 */
public class EnumSelectTag extends EnumItemTagSupport {

    @Override
    protected void resolveItems(final Iterable<?> items) throws IOException {
        print("<select", joinAttributes(), ">", Strings.ENTER);
        super.resolveItems(items);
        print("</select>", Strings.ENTER);
    }

    @Override
    protected void resolveItem(final String value, final String text) throws IOException {
        print("  <option value=", Strings.DOUBLE_QUOTES, value, Strings.DOUBLE_QUOTES);
        if (isCurrentValue(value)) {
            print(" selected=\"selected\"");
        }
        print(">", text, "</option>", Strings.ENTER);
    }
}
