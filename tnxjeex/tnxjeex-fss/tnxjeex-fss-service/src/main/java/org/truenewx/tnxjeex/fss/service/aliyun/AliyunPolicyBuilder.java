package org.truenewx.tnxjeex.fss.service.aliyun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;

/**
 * 阿里云授权方针文档构建器
 *
 * @author jianglei
 */
public class AliyunPolicyBuilder {

    private static String[] READ_OBJECT_ACTION_NAMES = { "GetObject", "GetObjectAcl", "ListParts" };

    private static String[] WRITE_OBJECT_ACTION_NAMES = { "PutObject", "PutObjectAcl",
            "DeleteObject", "AbortMultipartUpload" };

    private AliyunAccount account;

    public AliyunPolicyBuilder(AliyunAccount account) {
        this.account = account;
    }

    public String buildName(String prefix, String path) {
        return prefix + this.account.getOssBucket() + Strings.MINUS
                + EncryptUtil.encryptByMd5(path); // 加密路径以确保无特殊字符
    }

    public String buildReadDocument(String path) {
        return buildDocument(path, READ_OBJECT_ACTION_NAMES);
    }

    public String buildWriteDocument(String bucket, String path) {
        return buildDocument(path, WRITE_OBJECT_ACTION_NAMES);
    }

    public String buildDocument(String path, String[] actionNames) {
        Map<String, Object> policy = buildPolicyMap(path, actionNames);
        String document = JsonUtil.toJson(policy);
        return document;
    }

    private Map<String, Object> buildPolicyMap(String path, String[] actionNames) {
        Map<String, Object> policy = new HashMap<>();
        policy.put("Version", "1");

        List<Map<String, Object>> statements = new ArrayList<>();
        policy.put("Statement", statements);

        Map<String, Object> statement = new HashMap<>();
        statements.add(statement);

        List<String> actions = new ArrayList<>();
        statement.put("Action", actions);
        for (String actionName : actionNames) {
            actions.add(buildAction(actionName));
        }

        List<String> resources = new ArrayList<>();
        statement.put("Resource", resources);
        resources.add(buildResource(path));

        statement.put("Effect", "Allow");

        return policy;
    }

    private String buildAction(String actionName) {
        return "oss:" + actionName;
    }

    private String buildResource(String path) {
        String resource = "acs:oss:*:" + this.account.getAccountId() + Strings.COLON
                + this.account.getOssBucket() + Strings.SLASH + path;
        if (resource.endsWith(Strings.SLASH)) { // 为目录授权则追加*
            resource += Strings.ASTERISK;
        }
        return resource;
    }

}
