package org.truenewx.tnxjeex.fss.service.aliyun;

import org.slf4j.LoggerFactory;

import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;

/**
 * 阿里云STS临时角色扮演者
 *
 * @author jianglei
 */
public class AliyunStsRoleAssumer {

    private String roleArn;
    private long durationSeconds = 60 * 15L; // 允许的最小时间
    private AliyunAccount account;

    /**
     * @param account  阿里云账号
     * @param roleName RAM角色名称
     */
    public AliyunStsRoleAssumer(AliyunAccount account, String roleName) {
        this.account = account;
        this.roleArn = "acs:ram::" + account.getAccountId() + ":role/" + roleName.toLowerCase();
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public AssumeRoleResponse.Credentials assumeRole(String roleSessionName, String policyDocument) {
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(this.roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setPolicy(policyDocument);
        request.setDurationSeconds(this.durationSeconds);
        try {
            AssumeRoleResponse response = this.account.getAcsClient().getAcsResponse(request);
            return response.getCredentials();
        } catch (ClientException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }
}
