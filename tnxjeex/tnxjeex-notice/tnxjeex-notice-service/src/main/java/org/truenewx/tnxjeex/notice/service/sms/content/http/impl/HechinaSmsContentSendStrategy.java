package org.truenewx.tnxjeex.notice.service.sms.content.http.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjeex.notice.service.sms.content.http.AbstractHttpSmsContentSendStrategy;

/**
 * 巨象科技的短信发送策略
 *
 * @author liuzhiyi
 */
public class HechinaSmsContentSendStrategy extends AbstractHttpSmsContentSendStrategy {

    public HechinaSmsContentSendStrategy() {
        setEncoding("gb2312");
    }

    @Override
    public boolean isBatchable() {
        return false;
    }

    @Override
    public boolean isValid(String cellphone) {
        return true;
    }

    @Override
    public Map<String, Object> getParams(List<String> contents, int index, Set<String> cellphones) {
        Map<String, Object> params = this.defaultParams;
        if (params == null) {
            params = new HashMap<>();
        }
        // smsText
        StringBuffer contentString = new StringBuffer();
        if (index < 0) {
            for (String content : contents) {
                contentString.append(content);
            }
        } else {
            String content = contents.get(index);
            contentString.append(content);
        }
        params.put("msg", contentString.toString());

        // smsMob
        params.put("dst", StringUtils.join(cellphones, Strings.COMMA));
        return params;
    }

    @Override
    public Map<String, String> getFailures(int statusCode, String content) {
        return null;
    }

}
