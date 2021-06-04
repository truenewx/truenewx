package org.truenewx.tnxjeex.cas.core.validation;

import java.util.Date;
import java.util.Map;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;

/**
 * 便于序列化和反序列化的简单Assertion实现
 */
public class SimpleAssertion implements Assertion {

    private static final long serialVersionUID = -5843298720172335725L;

    private UserSpecificDetails<?> userDetails;
    private Date validFromDate;
    private Date validUntilDate;
    private Date authenticationDate;
    private Map<String, Object> attributes;
    private AttributePrincipal principal;

    public UserSpecificDetails<?> getUserDetails() {
        return this.userDetails;
    }

    public void setUserDetails(UserSpecificDetails<?> userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public Date getValidFromDate() {
        return this.validFromDate;
    }

    public void setValidFromDate(Date validFromDate) {
        this.validFromDate = validFromDate;
    }

    @Override
    public Date getValidUntilDate() {
        return this.validUntilDate;
    }

    public void setValidUntilDate(Date validUntilDate) {
        this.validUntilDate = validUntilDate;
    }

    @Override
    public Date getAuthenticationDate() {
        return this.authenticationDate;
    }

    public void setAuthenticationDate(Date authenticationDate) {
        this.authenticationDate = authenticationDate;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public AttributePrincipal getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(AttributePrincipal principal) {
        this.principal = principal;
    }

    @Override
    public boolean isValid() {
        if (this.validFromDate == null) {
            return true;
        }

        Date now = new Date();
        return (this.validFromDate.before(now) || this.validFromDate.equals(now))
                && (this.validUntilDate == null || this.validUntilDate.after(now) || this.validUntilDate.equals(now));
    }
}
