package org.truenewx.tnxsample.cas.web.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.truenewx.tnxsample.root.api.CustomerLoginApi;

/**
 * 客户登录客户端
 *
 * @author jianglei
 */
@FeignClient(name = "customer-login", url = "${tnxjee.common.apps.root.direct-uri}${tnxjee.common.apps.root.context-path}")
public interface CustomerLoginClient extends CustomerLoginApi {
}
