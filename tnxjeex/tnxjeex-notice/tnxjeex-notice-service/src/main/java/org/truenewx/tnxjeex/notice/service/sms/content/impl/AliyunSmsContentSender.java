package org.truenewx.tnxjeex.notice.service.sms.content.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjeex.notice.model.sms.SmsModel;
import org.truenewx.tnxjeex.notice.model.sms.SmsNotifyResult;
import org.truenewx.tnxjeex.notice.service.sms.content.AbstractSmsContentSender;
import org.truenewx.tnxjeex.openapi.client.service.aliyun.AliyunSmsAccessor;

/**
 * 阿里云的短信内容发送器
 *
 * @author jianglei
 */
public class AliyunSmsContentSender extends AbstractSmsContentSender {

    private AliyunSmsAccessor smsAccessor;
    private String templateCode;

    @Autowired
    public void setSmsAccessor(AliyunSmsAccessor smsAccessor) {
        this.smsAccessor = smsAccessor;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    @Override
    public SmsNotifyResult send(String signName, String content, int maxCount, String... cellphones) {
        SmsModel sms = new SmsModel();
        sms.setCellphones(cellphones);
        sms.setSendTime(new Date());
        SmsNotifyResult result = new SmsNotifyResult(sms);
        Map<String, String> failures = this.smsAccessor.send(signName, this.templateCode, content, cellphones);
        if (failures != null) {
            result.getFailures().putAll(failures);
        }
        return result;
    }


}
