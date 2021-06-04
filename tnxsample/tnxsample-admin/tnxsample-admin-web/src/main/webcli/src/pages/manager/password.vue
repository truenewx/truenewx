<template>
    <tnxel-submit-form ref="form" :model="model" :rules="rules">
        <el-form-item label="登录密码" prop="password">
            <el-col>
                <el-input type="password" v-model.trim="model.password"></el-input>
            </el-col>
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
            <el-col>
                <el-input type="password" v-model.trim="model.password2"></el-input>
            </el-col>
        </el-form-item>
    </tnxel-submit-form>
</template>

<script>
import {app, tnx, util} from '../../app';

export default {
    components: {
        'tnxel-submit-form': tnx.components.SubmitForm,
    },
    props: {
        id: {
            type: [String, Number],
            required: true,
        }
    },
    data() {
        const vm = this;
        return {
            model: {},
            rules: {
                password: [{
                    required: true,
                    message: '请填写登录密码'
                }, {
                    validator: function(rule, fieldValue, callback) {
                        if (vm.model.password2) {
                            vm.$refs.form.validateField('password2');
                        }
                        return callback();
                    }
                }],
                password2: [{
                    required: true,
                    message: '请填写确认密码'
                }, {
                    validator: function(rule, fieldValue, callback) {
                        const model = vm.model;
                        if (model.password && model.password2
                            && model.password !== model.password2) {
                            return callback(new Error('密码两次输入不一致'));
                        }
                        return callback();
                    }
                }]
            },
            oldPasswordError: false,
        };
    },
    computed: {
        md5Password: function() {
            return this.model.password ? util.md5(this.model.password) : '';
        },
    },
    methods: {
        dialog() {
            return {
                title: '修改密码',
                width: '20%',
                type: 'confirm',
                click: this.toSubmit,
            }
        },
        toSubmit(yes, close) {
            if (yes) {
                const vm = this;
                this.$refs.form.toSubmit(function() {
                    app.rpc.post('/manager/' + vm.id + '/reset-password', function() {
                        vm.$refs.form.disable();
                        tnx.toast('重置密码成功', function() {
                            close();
                        });
                    }, {
                        params: {
                            password: vm.md5Password,
                        }
                    });
                });
                return false;
            }
        }
    }
}
</script>
