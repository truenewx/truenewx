package org.truenewx.tnxjeex.fss.service.aliyun;

import org.apache.commons.lang3.StringUtils;

import com.aliyun.oss.OSS;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 阿里云账户信息提供者
 *
 * @author jianglei
 */
public class AliyunAccountProvider implements AliyunAccount {

    private String accountId;
    private String ossRegion;
    private String ossEndpoint;
    private String ossBucket;
    private String ramRegion = "cn-hangzhou";
    private String accessKeyId;
    private String accessKeySecret;
    private OSS oss;
    private IAcsClient acsClient;

    /**
     * @param accountId 阿里云账户编号
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @param ossRegion OSS区域
     */
    public void setOssRegion(String ossRegion) {
        this.ossRegion = ossRegion;
        if (StringUtils.isNotBlank(this.ossRegion)) {
            this.ossEndpoint = "oss-" + this.ossRegion + ".aliyuncs.com";
        } else {
            this.ossEndpoint = null;
        }
    }

    @Override
    public String getOssBucket() {
        return this.ossBucket;
    }

    /**
     * @param ossBucket OSS存储桶名称
     */
    public void setOssBucket(String ossBucket) {
        this.ossBucket = ossBucket;
    }

    /**
     * @param ramRegion RAM区域
     */
    public void setRamRegion(String ramRegion) {
        this.ramRegion = ramRegion;
    }

    /**
     * @param accessKeyId 账号访问id
     */
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    /**
     * @param accessKeySecret 账号访问密钥
     */
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    @Override
    public String getAccountId() {
        return this.accountId;
    }

    @Override
    public String getOssRegion() {
        return this.ossRegion;
    }

    @Override
    public String getOssEndpoint() {
        return this.ossEndpoint;
    }

    @Override
    public OSS getOssClient() {
        if (this.oss == null) {
            this.oss = AliyunOssUtil.buildOss(this.ossEndpoint, this.accessKeyId,
                    this.accessKeySecret);
        }
        return this.oss;
    }

    @Override
    public IAcsClient getAcsClient() {
        if (this.acsClient == null) {
            IClientProfile profile = DefaultProfile.getProfile(this.ramRegion,
                    this.accessKeyId, this.accessKeySecret);
            this.acsClient = new DefaultAcsClient(profile);
        }
        return this.acsClient;
    }

}
