package org.truenewx.tnxjee.webmvc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 转调控制器支持
 */
@RequestMapping("/recall")
public abstract class RecallControllerSupport extends JumpControllerSupport {

    @Override
    protected void jump(HttpServletRequest request, HttpServletResponse response, String targetUrl,
            Map<String, Object> body) throws Exception {
        HttpRequestBase httpRequest;
        if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            HttpPost httpPost = new HttpPost(targetUrl);
            String json = JsonUtil.toJson(body);
            String encoding = request.getCharacterEncoding();
            httpPost.setEntity(new StringEntity(json, encoding));
            httpRequest = httpPost;
        } else {
            httpRequest = new HttpGet(targetUrl);
        }
        WebUtil.getHeaders(request).forEach(httpRequest::setHeader);
        httpRequest.removeHeaders(HttpHeaders.CONTENT_LENGTH);
        try {
            CloseableHttpResponse recallResponse = HttpClientUtil.CLIENT.execute(httpRequest);
            for (Header header : recallResponse.getAllHeaders()) {
                response.setHeader(header.getName(), header.getValue());
            }
            response.setStatus(recallResponse.getStatusLine().getStatusCode());
            String responseData = EntityUtils.toString(recallResponse.getEntity());
            response.getWriter().write(responseData);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

}
