// tnxcore-wechat.js
/**
 * 微信登录客户端支持
 */
!function(a, b, c) {
    function d(a) {
        var c = "default";
        a.self_redirect === !0 ? c = "true" : a.self_redirect === !1 && (c = "false");
        var d = b.createElement("iframe"),
            e = window.location.protocol + "//open.weixin.qq.com/connect/qrconnect?appid=" + a.appid + "&scope=" + a.scope + "&redirect_uri=" + a.redirect_uri + "&state=" + a.state + "&login_type=jssdk&self_redirect=" + c + '&styletype=' + (a.styletype || '') + '&sizetype=' + (a.sizetype || '') + '&bgcolor=' + (a.bgcolor || '') + '&rst=' + (a.rst || '');
        e += a.style ? "&style=" + a.style : "", e += a.href ? "&href=" + a.href : "", d.src = e, d.frameBorder = "0", d.allowTransparency = "true", d.scrolling = "no", d.width = "300px", d.height = "400px";
        var f = b.getElementById(a.id);
        f.innerHTML = "", f.appendChild(d)
    }

    a.WxLogin = d
}(window, document);
// 以上来自于 http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js

export const WebApp = function WebApp(appId, productContextUri) {
    this.appId = appId;
    this.productContextUri = productContextUri; // 微信接口要求必须为生产环境域名
}

WebApp.prototype.login = function(containerId, redirectUri, options) {
    options = options || {};
    const protocol = window.location.protocol;
    const host = window.location.host;

    let url = protocol + '//' + this.productContextUri;
    if (this.productContextUri.startsWith(host)) {
        url += redirectUri;
    } else { // 不是生产环境则借助于生产环境的直接重定向能力进行再跳转
        url += '/redirect/';
        if (redirectUri.startsWith('/')) { // 目标跳转地址是相对地址，则加上当前网站根地址
            url += protocol.substr(0, protocol.length - 1) + '/' + host + redirectUri;
        } else { // 不以/开头，视为绝对地址
            let index = redirectUri.indexOf('://');
            if (index < 0) { // 绝对地址中一定包含://
                console.error('错误的跳转目标地址：' + redirectUri);
                return;
            }
            url += redirectUri.substr(0, index); // 协议部分
            url += redirectUri.substr(index + 2);
        }
    }

    let state = undefined;
    if (options.state) {
        state = JSON.stringify(options.state);
        state = window.tnx.util.base64.encode(state);
    }
    if (options.cssHref && options.cssHref.startsWith('/')) {
        options.cssHref = protocol + '//' + host + options.cssHref;
    }

    new window.WxLogin({
        id: containerId,
        appid: this.appId,
        scope: "snsapi_login",
        redirect_uri: encodeURI(url),
        href: options.cssHref,
        state: state,
    });
};

export default WebApp;
