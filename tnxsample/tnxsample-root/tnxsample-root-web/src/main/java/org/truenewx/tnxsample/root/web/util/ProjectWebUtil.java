package org.truenewx.tnxsample.root.web.util;

import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;

/**
 * WEB工程级工具集
 */
public class ProjectWebUtil {

    private ProjectWebUtil() {
    }

    public static UserSpecificDetails<IntegerUserIdentity> getCustomerDetails() {
        return SecurityUtil.getAuthorizedUserDetails();
    }

    public static Integer getCustomerId() {
        IntegerUserIdentity identity = SecurityUtil.getAuthorizedUserIdentity();
        return identity == null ? null : identity.getId();
    }

}
