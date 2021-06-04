package org.truenewx.tnxsample.admin.model.entity;

/**
 * 系统日志行
 *
 * @author jianglei
 */
public class SystemLogLine {

    private long pos;
    private String content;

    public SystemLogLine(long pos, String content) {
        this.pos = pos;
        this.content = content;
    }

    public long getPos() {
        return this.pos;
    }

    public String getContent() {
        return this.content;
    }

}
