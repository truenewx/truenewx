package org.truenewx.tnxjeex.notice.service.sms.content.impl;

import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjeex.notice.service.sms.content.AbstractSmsContentProvider;

/**
 * 阿里云短信内容提供者
 *
 * @author jianglei
 */
public class AliyunSmsContentProvider extends AbstractSmsContentProvider {

    @Override
    public String getContent(Map<String, Object> params, Locale locale) {
        return JsonUtil.toJson(params);
    }

}
