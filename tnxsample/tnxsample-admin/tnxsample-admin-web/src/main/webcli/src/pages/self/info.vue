<template>
    <tnxel-submit-form ref="form" :model="model" :rules="url">
        <el-form-item label="用户名">{{ model.username }}</el-form-item>
        <el-form-item label="是否超管">
            <div>
                <i class="fa" :class="model.top ? 'fa-check text-primary' : 'fa-ban text-muted'"></i>
            </div>
        </el-form-item>
        <el-form-item label="头像">
            <tnxel-fss-upload ref="headImageUpload" type="ManagerHeadImage" v-model="model.headImageUrl"
                v-if="model.username"/>
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
            <el-col :span="12">
                <el-input v-model.trim="model.fullName"/>
            </el-col>
        </el-form-item>
    </tnxel-submit-form>
</template>

<script>
import {app, tnx, util} from '../../app';

export default {
    components: {
        'tnxel-submit-form': tnx.components.SubmitForm,
        'tnxel-fss-upload': tnx.components.FssUpload,
    },
    props: ['opener'],
    data() {
        return {
            url: '/manager/self/info',
            model: {
                fullName: '',
                headImageFile: null,
            },
        };
    },
    created() {
        tnx.showLoading();
        const beginTime = new Date().getTime();
        const vm = this;
        app.rpc.get(this.url, model => {
            vm.model = model;
            util.function.setMinTimeout(beginTime, function() {
                tnx.closeLoading();
            }, 500);
        });
    },
    methods: {
        dialog() {
            return {
                title: '修改个人信息',
                width: '25%',
                type: 'confirm',
                click: this.toSubmit,
            }
        },
        toSubmit(yes, close) {
            if (yes) {
                const vm = this;
                this.$refs.form.toSubmit(function(form) {
                    if (tnx.validateUploaded(vm)) {
                        const model = vm.model;
                        const opener = vm.opener;
                        tnx.showLoading();
                        const beginTime = new Date().getTime();
                        app.rpc.post(vm.url, model, function() {
                            opener.managerCaption = model.fullName;
                            util.function.setMinTimeout(beginTime, function() {
                                vm.$refs.form.disable();
                                tnx.toast('修改成功', () => {
                                    close();
                                });
                            }, 500);
                        }, {form});
                    }
                });
                return false; // 不立即关闭对话框
            }
        }
    }
}
</script>
