package org.truenewx.tnxjeex.openapi.client.service.wechat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.tuple.Binate;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatAppType;

/**
 * 微信应用访问支持
 *
 * @author jianglei
 */
public abstract class WechatAppAccessSupport implements WechatAppAccessor {

    protected static final String HOST = "https://api.weixin.qq.com";

    protected Map<String, Object> get(String url, Map<String, Object> params) {
        try {
            Binate<Integer, String> response = HttpClientUtil.requestByGet(HOST + url, params);
            if (response != null) {
                String body = response.getRight();
                return JsonUtil.json2Map(body);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return new HashMap<>();
    }

    protected Map<String, Object> post(String url, Map<String, Object> params) {
        try {
            Binate<Integer, String> response = HttpClientUtil.requestByPost(HOST + url, params);
            if (response != null) {
                String body = response.getRight();
                return JsonUtil.json2Map(body);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    protected Map<String, Object> postFormData(String url, InputStream in, String mimeType) {
        HttpPost request = new HttpPost(HOST + url);
        request.addHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
        try {
            byte[] content = IOUtils.toByteArray(in);
            request.setEntity(new ByteArrayEntity(content, ContentType.create(mimeType)));
            CloseableHttpResponse response = HttpClientUtil.CLIENT.execute(request);
            String json = EntityUtils.toString(response.getEntity(), Strings.ENCODING_UTF8);
            return JsonUtil.json2Map(json);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * @return 应用类型
     */
    public abstract WechatAppType getAppType();

    /**
     * @return 应用id
     */
    public abstract String getAppId();

    /**
     * @return 访问秘钥
     */
    protected abstract String getSecret();

}