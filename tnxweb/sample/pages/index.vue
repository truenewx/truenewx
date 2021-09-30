<template>
    <div class="container-xl mx-auto flex-h-center">
        <div class="text-center">
            <h1>{{ title }}</h1>
            <p>首页</p>
            <el-space wrap>
                <el-button @click="showAlert">Alert</el-button>
                <el-button @click="showSuccess">Success</el-button>
                <el-button @click="showError">Error</el-button>
                <el-button @click="showConfirm">Confirm</el-button>
                <el-button @click="showToast">Toast</el-button>
                <el-button @click="showLoading">Loading</el-button>
                <el-button @click="showOpen">Open</el-button>
            </el-space>
            <div class="mt-5">
                <tnxel-fss-upload type="UserHeadImage" v-model="storageUrl"/>
            </div>
        </div>
    </div>
</template>

<script>
import {app, tnx} from '../tnx.js';
import info from './info.vue';

export default {
    components: {
        'tnxel-fss-upload': tnx.components.FssUpload,
    },
    data() {
        return {
            title: process.env.VUE_APP_TITLE,
            storageUrl: null,
        };
    },
    created() {
        app.rpc.loadConfig(process.env.VUE_APP_API_BASE_URL);
    },
    methods: {
        showAlert() {
            tnx.alert('Hello World', function() {
                console.info('Alerted');
            });
        },
        showSuccess() {
            tnx.success('提交成功，请等待组织管理员为你分配角色，获得组织角色后方可使用组织功能。', function() {
                console.info('Successed');
            });
        },
        showError() {
            tnx.error('Hello World', function() {
                console.info('Errored');
            });
        },
        showConfirm() {
            tnx.confirm('Hello World', function(yes) {
                console.info(yes);
            });
        },
        showToast() {
            tnx.toast('操作成功', 200000, function() {
                console.info('Toast closed.');
            });
        },
        showLoading() {
            tnx.showLoading('加载中');
            setTimeout(function() {
                tnx.closeLoading();
            }, 2000);
        },
        showOpen() {
            tnx.open(info, {
                param: '- from params',
                opener: this,
            });
        },
    }
}
</script>
