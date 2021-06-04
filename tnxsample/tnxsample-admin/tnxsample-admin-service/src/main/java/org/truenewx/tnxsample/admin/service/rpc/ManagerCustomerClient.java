package org.truenewx.tnxsample.admin.service.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.truenewx.tnxsample.root.api.ManagerCustomerApi;

@FeignClient(name = "manager-customer", url = "${tnxjee.common.apps.root.direct-uri}${tnxjee.common.apps.root.context-path}")
public interface ManagerCustomerClient extends ManagerCustomerApi {
}
