<template>
    <div class="text-center m-3">
        <h1>{{title}}</h1>
        <p>首页</p>
        <p>
            <el-button @click="showAlert">Alert</el-button>
            <el-button @click="showSuccess">Success</el-button>
            <el-button @click="showError">Error</el-button>
            <el-button @click="showConfirm">Confirm</el-button>
            <el-button @click="showToast">Toast</el-button>
            <el-button @click="showLoading">Loading</el-button>
            <el-button @click="showOpen">Open</el-button>
        </p>
        <p>
            <el-col class="text-left" :offset="11">
                <tnxel-upload class="mb-2" ref="headImageUpload" type="ManagerHeadImage"
                    v-if="uploadBaseUrl"/>
                <el-button type="primary" @click="uploadOk">确定</el-button>
            </el-col>
        </p>
    </div>
</template>

<script>
    import {app, tnx} from '../app.js';
    import info from './info.vue';

    export default {
        data () {
            return {
                title: process.env.VUE_APP_TITLE,
                uploadBaseUrl: app.rpc.apps.fss,
            };
        },
        created () {
            if (!this.uploadBaseUrl) {
                const vm = this;
                app.rpc.loadConfig(function(context) {
                    vm.uploadBaseUrl = context.apps.fss;
                });
            }
        },
        methods: {
            showAlert () {
                tnx.alert('Hello World', function() {
                    console.info('Alerted');
                });
            },
            showSuccess () {
                tnx.success('Hello World', function() {
                    console.info('Successed');
                });
            },
            showError () {
                tnx.error('Hello World', function() {
                    console.info('Errored');
                });
            },
            showConfirm () {
                tnx.confirm('Hello World', function(yes) {
                    console.info(yes);
                });
            },
            showToast () {
                tnx.toast('操作成功', 2000, function() {
                    console.info('Toast closed.');
                });
            },
            showLoading () {
                tnx.showLoading('加载中');
                setTimeout(function() {
                    tnx.closeLoading();
                }, 2000);
            },
            showOpen () {
                tnx.open(info, {
                    param: '- from params',
                    opener: this,
                });
            },
            uploadOk () {
                this.$refs.headImageUpload.getStorageUrls().then(function(storageUrls) {
                    if (storageUrls.length) {
                        tnx.success(storageUrls.join('\n'));
                    }
                }).catch(function(file) {
                    tnx.alert('文件"' + file.name + '"还未上传完毕，请稍候');
                });
            }
        }
    }
</script>
