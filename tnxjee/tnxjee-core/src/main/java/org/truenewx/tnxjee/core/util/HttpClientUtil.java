package org.truenewx.tnxjee.core.util;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.HttpRequestMethod;
import org.truenewx.tnxjee.core.util.tuple.Binary;
import org.truenewx.tnxjee.core.util.tuple.Binate;

/**
 * Http客户端工具类
 *
 * @author jianglei
 */
public class HttpClientUtil {

    public static final CloseableHttpClient CLIENT = HttpClientBuilder.create().build();

    private HttpClientUtil() {
    }

    private static List<NameValuePair> toNameValuePairs(Map<String, Object> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Iterable) {
                for (Object element : (Iterable<?>) value) {
                    if (element != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), element.toString()));
                    }
                }
            } else if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    Object element = Array.get(value, i);
                    if (element != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), element.toString()));
                    }
                }
            } else {
                pairs.add(new BasicNameValuePair(entry.getKey(), value.toString()));
            }
        }
        return pairs;
    }

    private static CloseableHttpResponse execute(String url, Map<String, Object> params,
            HttpRequestMethod method, String encoding, int timeout) throws Exception {
        HttpRequestBase request;
        switch (method) {
            case GET:
                request = new HttpGet(NetUtil.mergeParams(url, params, null));
                break;
            case POST:
                HttpPost post = new HttpPost(url);
                post.setEntity(new UrlEncodedFormEntity(toNameValuePairs(params), encoding));
                request = post;
                break;
            default:
                request = null;
        }
        if (request != null) {
            if (timeout > 0) {
                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(timeout).setConnectTimeout(timeout)
                        .setSocketTimeout(timeout).build();
                request.setConfig(requestConfig);
            }
            return CLIENT.execute(request);
        }
        return null;
    }

    public static Binate<Integer, String> request(String url, Map<String, Object> params,
            HttpRequestMethod method, String encoding) throws Exception {
        CloseableHttpResponse response = execute(url, params, method, encoding, 0);
        if (response != null) {
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                String content = EntityUtils.toString(response.getEntity(), encoding);
                return new Binary<>(statusCode, content);
            } finally {
                // 确保关闭请求连接
                response.close();
            }
        }
        return null;
    }

    public static Binate<Integer, String> requestByGet(String url, Map<String, Object> params)
            throws Exception {
        return request(url, params, HttpRequestMethod.GET, Strings.ENCODING_UTF8);
    }

    public static Binate<Integer, String> requestByPost(String url, Map<String, Object> params)
            throws Exception {
        return request(url, params, HttpRequestMethod.POST, Strings.ENCODING_UTF8);
    }

    public static InputStream getImageByPostJson(String url, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        try {
            StringEntity entity = new StringEntity(JsonUtil.toJson(params));
            entity.setContentType("image/png"); // png比jpg具有更大的适应性，固定为png
            httpPost.setEntity(entity);
            HttpResponse response = CLIENT.execute(httpPost);
            return response.getEntity().getContent();
        } catch (Exception e) {
            LogUtil.error(HttpClientUtil.class, e);
        }
        return null;
    }

}
