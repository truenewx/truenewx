import '../src/tnxcore.css';
import 'bootstrap/dist/css/bootstrap-reboot.min.css';
import 'bootstrap/dist/css/bootstrap-grid.min.css';
import 'bootstrap/dist/css/bootstrap-utilities.min.css';
import '../src/vue/tnxvue.css';
import 'element-plus/dist/index.css';
import '../src/vue/element-plus/tnxel.css';
import tnx from './tnx';
import App from './App.vue';

tnx.createVueInstance(App).mount('#app');
