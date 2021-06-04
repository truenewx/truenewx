// app.js
import tnxjq from '../../../../../../../tnxweb/components/jquery/tnxjq';
import tnxel from '../../../../../../../tnxweb/components/vue/element/tnxel.js';

export const tnx = Object.assign({}, tnxjq, tnxel, {
    libs: Object.assign({}, tnxjq.libs, tnxel.libs)
});
window.tnx = tnx; // tnx已被修改，需重新赋值给window
export const $ = tnx.libs.$;
export const Vue = tnx.libs.Vue;
export const util = tnx.util;
export const app = tnx.app;

app.context = process.env.VUE_APP_VIEW_BASE_URL;

app.rpc.toLogin = function(loginFormUrl, originalUrl) {
    if (loginFormUrl) {
        let alertable = originalUrl !== undefined;
        const username = process.env.VUE_APP_LOGIN_USERNAME;
        const password = process.env.VUE_APP_LOGIN_PASSWORD;
        if (username && password) { // 将默认用户名密码插入到参数清单头部，以免被其它参数中的#影响而被忽略
            const index = loginFormUrl.indexOf('?') + 1;
            loginFormUrl = loginFormUrl.substr(0, index) + 'username=' + username + '&password=' + password
                + '&' + loginFormUrl.substr(index);
            alertable = false;
        }
        if (alertable) {
            tnx.alert('尚未登录或登录会话已过期，需重新登录', function() {
                window.location.href = loginFormUrl;
            });
        } else {
            window.location.href = loginFormUrl;
        }
        return true;
    }
    return false;
}

export default app;
