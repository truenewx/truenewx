<template>
    <div class="container-xl mx-auto flex-h-center">
        <div class="text-center">
            <h1>{{ title }}</h1>
            <p>首页</p>
            <p>
                <a-button @click="showAlert">Alert</a-button>
                <a-button @click="showSuccess">Success</a-button>
                <a-button @click="showError">Error</a-button>
                <a-button @click="showConfirm">Confirm</a-button>
                <a-button @click="showToast">Toast</a-button>
                <a-button @click="showLoading">Loading</a-button>
                <a-button @click="showOpen">Open</a-button>
            </p>
        </div>
    </div>
</template>

<script>
import {app, tnx} from '../app.js';
import info from './info.vue';

export default {
    data() {
        return {
            title: process.env.VUE_APP_TITLE,
            uploadBaseUrl: app.rpc.apps.fss,
        };
    },
    created() {
        // if (!this.uploadBaseUrl) {
        //     const vm = this;
        //     app.rpc.loadConfig(function(context) {
        //         vm.uploadBaseUrl = context.apps.fss;
        //     });
        // }
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
            tnx.toast('操作成功', 2000, function() {
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
