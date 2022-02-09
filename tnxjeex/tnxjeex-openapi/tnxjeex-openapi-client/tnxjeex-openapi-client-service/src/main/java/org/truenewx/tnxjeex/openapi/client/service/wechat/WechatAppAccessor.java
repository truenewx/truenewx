package org.truenewx.tnxjeex.openapi.client.service.wechat;

import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatUser;

/**
 * 微信应用访问器
 *
 * @author jianglei
 */
public interface WechatAppAccessor {

    /**
     * 根据登录编码获取微信用户信息
     *
     * @param loginCode 登录编码
     * @return 微信用户信息，如果登录编码无效则返回null
     */
    WechatUser getUser(String loginCode);

    /**
     * 根据登录编码加载微信用户信息<br/>
     * 注意：一个登录编码通过本方法加载一次后将失效，这意味着本方法不是幂等的
     *
     * @param loginCode 登录编码
     * @return 微信用户信息
     * @throws BusinessException 如果登录编码无效
     */
    default WechatUser loadUser(String loginCode) {
        WechatUser user = getUser(loginCode);
        if (user == null) { // 无效的微信登录编码
            throw new BusinessException(WechatOpenApiClientExceptionCodes.INVALID_LOGIN_CODE);
        }
        return user;
    }

}
