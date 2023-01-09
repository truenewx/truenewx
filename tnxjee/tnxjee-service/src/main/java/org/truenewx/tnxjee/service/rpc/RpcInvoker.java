package org.truenewx.tnxjee.service.rpc;

import java.util.Map;

import org.truenewx.tnxjee.core.spec.HttpRequestMethod;

/**
 * RPC调用器
 *
 * @author jianglei
 */
public interface RpcInvoker {

    String invoke(HttpRequestMethod method, String url, Map<String, Object> params, String type);

}
