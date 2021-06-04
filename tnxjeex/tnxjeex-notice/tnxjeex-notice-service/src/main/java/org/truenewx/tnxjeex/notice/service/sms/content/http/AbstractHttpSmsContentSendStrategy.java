package org.truenewx.tnxjeex.notice.service.sms.content.http;

import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.HttpRequestMethod;

/**
 * 抽象的HTTP短信发送策略
 *
 * @author jianglei
 */
public abstract class AbstractHttpSmsContentSendStrategy implements HttpSmsContentSendStrategy {

    private String url;
    private HttpRequestMethod requestMethod = HttpRequestMethod.POST;
    private String encoding = Strings.ENCODING_UTF8;
    protected Map<String, Object> defaultParams;

    @Override
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public HttpRequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(HttpRequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setDefaultParams(Map<String, Object> defaultParams) {
        this.defaultParams = defaultParams;
    }

}
