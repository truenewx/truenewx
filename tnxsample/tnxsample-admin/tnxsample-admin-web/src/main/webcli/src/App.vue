<template>
    <div id="app" class="d-flex flex-column" v-if="logined">
        <el-header height="55px" class="border-bottom fixed-top d-flex align-items-center"
            theme="primary">
            <layout-header/>
        </el-header>
        <div class="flex-grow-1 d-flex page-container">
            <el-aside width="auto">
                <layout-menu/>
            </el-aside>
            <el-main class="flex-grow-1 border-left d-flex flex-column" :class="{'bg-muted':!home}">
                <layout-breadcrumb v-if="!home"/>
                <div class="flex-grow-1 border bg-white p-3" v-if="!home">
                    <router-view></router-view>
                </div>
                <page-index v-else/>
            </el-main>
        </div>
        <el-footer height="auto" class="border-top p-3">
            <layout-footer/>
        </el-footer>
    </div>
</template>

<script>
import app from './app.js';
import header from './layout/header.vue';
import menu from './layout/menu.vue';
import breadcrumb from "./layout/breadcrumb";
import footer from './layout/footer.vue';
import index from './pages/index.vue';

export default {
    name: 'App',
    components: {
        'layout-header': header,
        'layout-menu': menu,
        'layout-breadcrumb': breadcrumb,
        'layout-footer': footer,
        'page-index': index,
    },
    data() {
        return {
            logined: false,
        }
    },
    computed: {
        home() {
            return this.$route.path === "/";
        }
    },
    created() {
        const vm = this;
        app.rpc.loadConfig(process.env.VUE_APP_API_BASE_URL, () => {
            app.rpc.ensureLogined(function() {
                vm.logined = true;
            });
        });
    }
}
</script>
