package org.truenewx.tnxsample.admin.web.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.truenewx.tnxjeex.fss.api.FssMetaResolver;

@FeignClient(name = "fss-meta", url = "${tnxjee.common.apps.fss.direct-uri}${tnxjee.common.apps.fss.context-path}")
public interface FssMetaClient extends FssMetaResolver {
}
