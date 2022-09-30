import {app} from '@/tnx';

// 获取验证图片以及token
export function reqGet(data) {
    return new Promise(resolve => {
        app.rpc.post('/captcha/generate', data, function(res) {
            resolve(res);
        });
    });
}

// 滑动或者点选验证
export function reqCheck(data) {
    return new Promise(resolve => {
        app.rpc.post('/captcha/check', data, function(res) {
            resolve(res);
        });
    });
}


