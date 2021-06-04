import '../../../../../../../tnxweb/components/tnxcore.css';
import '../../../../../../../tnxweb/components/bootstrap/base-4.5.3.css';
import 'element-ui/lib/theme-chalk/index.css';
import '@fortawesome/fontawesome-free/css/all.css';
import '../../../../../../../tnxweb/components/vue/tnxvue.css';
import '../../../../../../../tnxweb/components/vue/element/tnxel.css';
import '../../../../../../../tnxweb/components/vue/element/tnxel-theme.css';
import './assets/app.css';
import {app, Vue} from './app';
import router from './router.js';
import App from './App.vue';

Vue.config.productionTip = app.isProduction();

new Vue({
    router: router,
    render: h => h(App),
}).$mount('#app');
