package org.truenewx.tnxjee.model.validation.rule;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Html标签限定规则
 *
 * @author jianglei
 */
public class HtmlTagLimitRule extends ValidationRule {

    private Set<String> allowed = new LinkedHashSet<>();
    private Set<String> forbidden = new LinkedHashSet<>();

    public Set<String> getAllowed() {
        return Collections.unmodifiableSet(this.allowed);
    }

    public Set<String> getForbidden() {
        return Collections.unmodifiableSet(this.forbidden);
    }

    public void addAllowed(String... allowed) {
        for (String tag : allowed) {
            this.allowed.add(tag.toLowerCase());
        }
    }

    public void addForbidden(String... forbidden) {
        for (String tag : forbidden) {
            this.forbidden.add(tag.toLowerCase());
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

}
