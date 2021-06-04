package org.truenewx.tnxjeex.openapi.client.service.wechat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.HttpClientUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatAppType;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatUser;

/**
 * 微信小程序访问器
 *
 * @author jianglei
 */
public abstract class WechatMpAccessor extends WechatPublicAppAccessSupport {

    @Override
    public final WechatAppType getAppType() {
        return WechatAppType.MP;
    }

    @Override
    public WechatUser getUser(String loginCode) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appid", getAppId());
        params.put("secret", getSecret());
        params.put("js_code", loginCode);
        params.put("grant_type", "authorization_code");
        Map<String, Object> result = get("/sns/jscode2session", params);
        String openId = (String) result.get("openid");
        if (StringUtils.isNotBlank(openId)) { // openId不能为空
            WechatUser user = new WechatUser();
            user.setAppType(getAppType());
            user.setOpenId(openId);
            user.setUnionId((String) result.get("unionid"));
            user.setSessionKey((String) result.get("session_key"));
            return user;
        }
        return null;
    }

    /**
     * 获取不限数量的小程序码图片
     *
     * @param scene   场景参数字符串，长度不能超过32位
     * @param page    打开的页面路径，为空时默认进入首页
     * @param width   图片宽度，默认430，最小280，最大1280，单位：px
     * @param color   主色调颜色值，形如：#rrggbb
     * @param hyaline 背景是否透明
     * @return 小程序图片的输入流
     */
    public InputStream getUnlimitedWxacodeImage(String scene, String page, Integer width, String color,
            boolean hyaline) {
        if (scene.length() > 32) {
            return null;
        }
        String url = HOST + "/wxa/getwxacodeunlimit?access_token=" + getAccessToken();
        Map<String, Object> params = new HashMap<>();
        params.put("scene", scene);
        params.put("is_hyaline", hyaline);
        params.put("auto_color", Boolean.TRUE);
        if (page != null) {
            params.put("page", page);
        }
        if (width != null) {
            params.put("width", width);
        }
        // 仅支持形如#rrggbb的颜色值
        if (color != null && color.startsWith(Strings.WELL) && color.length() == 7) {
            int r = MathUtil.hex2Int(color.substring(1, 3), -1);
            int g = MathUtil.hex2Int(color.substring(3, 5), -1);
            int b = MathUtil.hex2Int(color.substring(5, 7), -1);
            if (r >= 0 && g >= 0 && b >= 0) {
                Map<String, String> colorMap = new HashMap<>();
                colorMap.put("r", String.valueOf(r));
                colorMap.put("g", String.valueOf(g));
                colorMap.put("b", String.valueOf(b));
                // 官方API文档中有错误，参数名称应为以下名称，取值应为字符串类型
                params.put("line_color", colorMap);
                params.put("auto_color", Boolean.FALSE);
            }
        }
        return HttpClientUtil.getImageByPostJson(url, params);
    }

    public String getScene(Map<String, Object> parameters) {
        StringBuffer scene = new StringBuffer();
        // 为避免出现=，形如：a-1;b-2，所以key和value中都不能带减号和分号
        parameters.forEach((key, value) -> {
            String sValue = value.toString();
            if (!key.contains(Strings.SEMICOLON) && !key.contains(Strings.MINUS) && !sValue.contains(Strings.SEMICOLON)
                    && !sValue.contains(Strings.MINUS)) {
                scene.append(Strings.SEMICOLON).append(key).append(Strings.MINUS).append(sValue);
            }
        });
        if (scene.length() > 0) {
            scene.deleteCharAt(0); // 去掉首位的分号
        }
        return scene.toString();
    }

}
