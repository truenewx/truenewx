package org.truenewx.tnxjee.webmvc.util;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.truenewx.tnxjee.service.rpc.RpcInvoker;

public interface WebRpcInvoker extends RpcInvoker {

    Map<String, String> generateHeaders(String type);

    void download(HttpServletResponse response, String url, Map<String, Object> params, String type);

}
