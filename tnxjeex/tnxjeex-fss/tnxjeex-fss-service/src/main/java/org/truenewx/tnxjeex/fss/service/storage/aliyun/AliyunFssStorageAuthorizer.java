package org.truenewx.tnxjeex.fss.service.storage.aliyun;

import java.util.Date;

import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.DateUtil;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageAuthorizer;
import org.truenewx.tnxjeex.fss.service.storage.FssStorageProvider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectAcl;
import com.aliyun.oss.model.ObjectPermission;
import com.aliyuncs.auth.sts.AssumeRoleResponse;

/**
 * 阿里云的文件存储授权器
 *
 * @author jianglei
 */
public class AliyunFssStorageAuthorizer implements FssStorageAuthorizer {

    private int tempReadExpiredSeconds = 60; // 临时读取时限默认60秒
    private AliyunAccount account;
    private AliyunPolicyBuilder policyBuilder;
    private AliyunStsRoleAssumer readStsRoleAssumer;
    private String contextUrl;

    public AliyunFssStorageAuthorizer(AliyunAccount account) {
        this.account = account;
        this.policyBuilder = new AliyunPolicyBuilder(account);
        // 默认的上下文地址以//开头，不包含具体访问协议，与访问者当前使用协议相同
        this.contextUrl = "//" + this.account.getOssBucket() + Strings.DOT + this.account.getOssEndpoint();
    }

    /**
     * @param tempReadExpiredSeconds 临时读取权限过期秒数
     */
    public void setTempReadExpiredSeconds(int tempReadExpiredSeconds) {
        this.tempReadExpiredSeconds = tempReadExpiredSeconds;
    }

    /**
     * @param readStsRoleName 读权限的STS临时扮演的RAM角色名称
     */
    public void setReadStsRoleName(String readStsRoleName) {
        this.readStsRoleAssumer = new AliyunStsRoleAssumer(this.account, readStsRoleName);
    }

    @Override
    public FssStorageProvider getProvider() {
        return FssStorageProvider.ALIYUN;
    }

    @Override
    public void authorizePublicRead(String path) {
        path = AliyunOssUtil.standardizePath(path);
        this.account.getOssClient().setObjectAcl(this.account.getOssBucket(), path, CannedAccessControlList.PublicRead);
    }

    private boolean isPublicRead(String path) {
        path = AliyunOssUtil.standardizePath(path);
        ObjectAcl acl = this.account.getOssClient().getObjectAcl(this.account.getOssBucket(), path);
        ObjectPermission permission = acl.getPermission();
        return permission == ObjectPermission.PublicRead || permission == ObjectPermission.PublicReadWrite;
    }

    @Override
    public String getContextUrl() {
        return this.contextUrl;
    }

    protected void setContextUrl(String contextUrl) {
        this.contextUrl = contextUrl;
    }

    @Override
    public String getReadUrl(UserIdentity<?> userIdentity, String path) {
        path = AliyunOssUtil.standardizePath(path);
        // 拆分请求参数，确保路径不带参数
        int index = path.indexOf(Strings.QUESTION);
        String parameterString = Strings.EMPTY;
        if (index >= 0) {
            parameterString = path.substring(index + 1);
            path = path.substring(0, index);
        }
        try {
            if (isPublicRead(path)) {
                StringBuilder url = new StringBuilder(getContextUrl()).append(Strings.SLASH).append(path);
                if (parameterString.length() > 0) {
                    url.append(Strings.QUESTION).append(parameterString);
                }
                return url.toString();
            } else if (this.readStsRoleAssumer != null) { // 非公开可读的，授予临时读取权限
                String policyDocument = this.policyBuilder.buildReadDocument(path);
                AssumeRoleResponse.Credentials credentials = this.readStsRoleAssumer.assumeRole(userIdentity.toString(),
                        policyDocument);
                if (credentials != null) {
                    OSS oss = AliyunOssUtil.buildOss(this.account.getOssEndpoint(), credentials.getAccessKeyId(),
                            credentials.getAccessKeySecret(), credentials.getSecurityToken());
                    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(this.account.getOssBucket(),
                            path);
                    Date expiration = DateUtil.addSeconds(new Date(), this.tempReadExpiredSeconds);
                    request.setExpiration(expiration);
                    if (parameterString.length() > 0) {
                        String[] params = parameterString.split(Strings.AND);
                        for (String param : params) {
                            String[] array = param.split(Strings.EQUAL);
                            if (array.length > 1) {
                                request.addQueryParameter(array[0], array[1]);
                            }
                        }
                    }
                    String url = oss.generatePresignedUrl(request).toString();
                    url = replaceContextUrl(url, getContextUrl());
                    return url;
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }

    private String replaceContextUrl(String url, String contextUrl) {
        int index = url.indexOf("://");
        url = url.substring(url.indexOf(Strings.SLASH, index + 3));
        return contextUrl + url;
    }

}
