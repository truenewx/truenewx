package org.truenewx.tnxjeex.notice.model.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 短信模型
 *
 * @author jianglei
 */
public class SmsModel implements Sms {
    /**
     * 内容清单
     */
    private List<String> contents;
    /**
     * 手机号码清单
     */
    private String[] cellphones;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 接收时间
     */
    private Date receiveTime;

    @Override
    public List<String> getContents() {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        return this.contents;
    }

    /**
     * @param contents 内容清单
     */
    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public String[] getCellphones() {
        if (this.cellphones == null) {
            this.cellphones = new String[0];
        }
        return this.cellphones;
    }

    /**
     * @param cellphones 手机号码清单
     */
    public void setCellphones(String... cellphones) {
        this.cellphones = cellphones;
    }

    @Override
    public Date getSendTime() {
        return this.sendTime;
    }

    /**
     * @param sendTime 发送时间
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public Date getReceiveTime() {
        return this.receiveTime;
    }

    /**
     * @param receiveTime 接收时间
     */
    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }
}
