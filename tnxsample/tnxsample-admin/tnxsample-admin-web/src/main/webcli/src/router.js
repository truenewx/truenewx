// router.js
import {tnx} from './app';
import VueRouter from 'vue-router';
import menu from './menu.js';

const router = tnx.buildRouter(VueRouter, menu, function(path) {
    if (path === '/') {
        path = '/index';
    }
    return import('./pages' + path + '.vue');
});

export default router;
