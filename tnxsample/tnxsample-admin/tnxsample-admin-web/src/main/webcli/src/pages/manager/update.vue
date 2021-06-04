<template>
    <tnxel-submit-form ref="form" :model="model" rules="/manager/*/update" @rules-loaded="onRulesLoaded"
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
        <el-form-item label="所属角色" prop="roleIds">
            <el-col :span="12">
                <tnxel-tag-select ref="role" items="/role/list" key-name="id" text-name="name"
                    type="warning" :keys="roleIds"/>
            </el-col>
        </el-form-item>
    </tnxel-submit-form>
</template>

<script>
import {app, tnx} from '../../app';

export default {
    components: {
        'tnxel-submit-form': tnx.components.SubmitForm,
        'tnxel-tag-select': tnx.components.TagSelect,
    },
    data() {
        return {
            model: {},
        };
    },
    computed: {
        roleIds() {
            if (this.model.roles) {
                const roleIds = [];
                this.model.roles.forEach(role => {
                    roleIds.push(role.id);
                });
                return roleIds;
            }
            return undefined;
        }
    },
    created() {
        const managerId = this.$route.params.id;
        const vm = this;
        app.rpc.get('/manager/' + managerId, model => {
            vm.model = model;
        });
    },
    methods: {
        onRulesLoaded(rules) {
            delete rules.password;
        },
        submit() {
            const vm = this;
            const managerId = vm.$route.params.id;
            const model = Object.assign({}, vm.model, {
                roles: undefined,
                roleIds: vm.$refs.role.getSelectedKeys(),
            });
            app.rpc.post('/manager/' + managerId + '/update', model, function() {
                vm.$refs.form.disable();
                tnx.toast('修改成功', function() {
                    vm.$router.back();
                });
            });
        }
    }
}
</script>
