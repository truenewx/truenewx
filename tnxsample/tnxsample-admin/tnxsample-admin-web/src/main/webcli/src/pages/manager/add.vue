<template>
    <tnxel-submit-form ref="form" :model="model" :rules="url" @rules-loaded="onRulesLoaded"
        :submit="submit">
        <el-form-item label="工号" prop="jobNo">
            <el-col :span="12">
                <el-input v-model.trim="model.jobNo"/>
            </el-col>
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
            <el-col :span="12">
                <el-input v-model.trim="model.fullName"/>
            </el-col>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
            <el-col :span="12">
                <el-input v-model.trim="model.username"/>
            </el-col>
        </el-form-item>
        <el-form-item label="登录密码" prop="password">
            <el-col :span="12">
                <el-input type="password" v-model.trim="model.password"></el-input>
            </el-col>
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
            <el-col :span="12">
                <el-input type="password" v-model.trim="model.password2"></el-input>
            </el-col>
        </el-form-item>
        <el-form-item label="所属角色" prop="roleIds">
            <el-col :span="12">
                <tnxel-tag-select ref="role" items="/role/list" key-name="id" text-name="name"
                    type="warning"/>
            </el-col>
        </el-form-item>
    </tnxel-submit-form>
</template>

<script>
import {app, tnx, util} from '../../app';

export default {
    components: {
        'tnxel-submit-form': tnx.components.SubmitForm,
        'tnxel-tag-select': tnx.components.TagSelect,
    },
    data() {
        return {
            url: '/manager/add',
            model: {},
        };
    },
    computed: {
        md5Password: function() {
            return this.model.password ? util.md5(this.model.password) : '';
        },
    },
    methods: {
        onRulesLoaded(rules) {
            const vm = this;
            const passwordRules = rules.password;
            const password2Rules = [].concat(passwordRules);
            if (password2Rules[0].required) {
                password2Rules[0].message = '确认密码不能为空';
            }
            password2Rules.push({
                validator: function(rule, fieldValue, callback) {
                    const model = vm.model;
                    if (model.password && model.password2
                        && model.password !== model.password2) {
                        return callback(new Error('登录密码两次输入不一致'));
                    }
                    return callback();
                }
            });
            passwordRules.push({
                validator: function(rule, fieldValue, callback) {
                    if (vm.model.password2) {
                        vm.$refs.form.validateField('password2');
                    }
                    return callback();
                }
            });
            rules.password2 = password2Rules;
        },
        submit() {
            const vm = this;
            tnx.confirm('管理员账号创建后无法删除，只能禁用，请谨慎操作。确定要提交吗？', yes => {
                if (yes) {
                    const model = Object.assign({}, vm.model, {
                        password: vm.md5Password,
                        roleIds: vm.$refs.role.getSelectedKeys(),
                    });
                    app.rpc.post(vm.url, model, function() {
                        vm.$refs.form.disable();
                        tnx.toast('新增成功', function() {
                            vm.$router.back();
                        });
                    });
                }
            });
        }
    }
}
</script>
