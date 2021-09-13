import '../components/tnxcore.css';
import 'bootstrap/dist/css/bootstrap-reboot.min.css';
import 'bootstrap/dist/css/bootstrap-grid.min.css';
import 'bootstrap/dist/css/bootstrap-utilities.min.css';
import '../components/vue/tnxvue.css';
import 'ant-design-vue/dist/antd.min.css';
import '../components/vue/ant-design/tnxad.css';
import tnx from './tnx';
import App from './App.vue';

tnx.createVueInstance(App).mount('#app');
