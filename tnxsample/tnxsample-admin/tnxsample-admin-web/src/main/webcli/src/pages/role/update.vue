<template>
    <tnxel-submit-form ref="form" :model="model" rules="/role/*/update" :submit="submit">
        <el-form-item label="名称" prop="name">
            <el-col :span="9">
                <el-input v-model.trim="model.name"/>
            </el-col>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
            <el-col :span="18">
                <el-input v-model.trim="model.remark"/>
            </el-col>
        </el-form-item>
        <el-form-item label="操作权限">
            <el-col :span="18">
                <tnxel-permission-tree class="border" ref="permissionTree" :menu="menu"
                    :permissions="model.permissions"/>
            </el-col>
        </el-form-item>
        <el-form-item label="包含管理员" prop="managerIds">
            <el-col :span="18">
                <tnxel-tag-select ref="manager" items="/manager/list" :to-tag="toManagerTag"
                    :keys="managerIds"/>
            </el-col>
        </el-form-item>
    </tnxel-submit-form>
</template>

<script>
import {app, tnx} from '../../app';
import menu from '../../menu';

export default {
    components: {
        'tnxel-submit-form': tnx.components.SubmitForm,
        'tnxel-permission-tree': tnx.components.PermissionTree,
    },
    data() {
        return {
            menu: menu,
            model: {
                name: '',
                remark: '',
            },
        };
    },
    computed: {
        managerIds() {
            if (this.model.managers) {
                const managerIds = [];
                this.model.managers.forEach(manager => {
                    managerIds.push(manager.id);
                });
                return managerIds;
            }
            return undefined;
        }
    },
    created() {
        const roleId = this.$route.params.id;
        const vm = this;
        app.rpc.get('/role/' + roleId, model => {
            vm.model = model;
        });
    },
    methods: {
        toManagerTag(manager) {
            let text = manager.fullName + ' (' + manager.username;
            if (manager.jobNo) {
                text += '#' + manager.jobNo;
            }
            text += ')';
            return {
                key: manager.id,
                text: text,
            }
        },
        submit() {
            const roleId = this.$route.params.id;
            const model = Object.assign({}, this.model, {
                permissions: this.$refs.permissionTree.getPermissions(),
                managerIds: this.$refs.manager.getSelectedKeys(),
            });
            const vm = this;
            app.rpc.post('/role/' + roleId + '/update', model, function() {
                vm.$refs.form.disabled = true;
                tnx.toast('修改成功', function() {
                    vm.$router.back();
                });
            });
        }
    }
}
</script>
