package org.truenewx.tnxjeex.notice.service.sms.content;

import java.util.List;

import org.truenewx.tnxjeex.notice.model.sms.SmsNotifyResult;

/**
 * 可分割的短信内容发送器
 *
 * @author jianglei
 */
public abstract class SplitableSmsContentSender extends AbstractSmsContentSender {

    private SmsContentSpliter spliter = new DefaultSmsContentSpliter();

    /**
     * @param spliter 短信内容分割器
     */
    public void setSpliter(SmsContentSpliter spliter) {
        this.spliter = spliter;
    }

    @Override
    public SmsNotifyResult send(String signName, String content, int maxCount, String... cellphones) {
        List<String> contents = this.spliter.split(content, maxCount);
        return send(signName, contents, cellphones);
    }

    /**
     * 分成指定条数的内容发送短信
     *
     * @param signName   签名
     * @param contents   内容清单，每一个内容为一条短信
     * @param cellphones 手机号码清单
     * @return 发送结果
     */
    protected abstract SmsNotifyResult send(String signName, List<String> contents, String... cellphones);

}
