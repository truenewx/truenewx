<template>
    <tnxel-submit-form ref="form" :model="model" :rules="url" :submit="submit">
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
                <tnxel-permission-tree class="border" ref="permissionTree" :menu="menu"/>
            </el-col>
        </el-form-item>
        <el-form-item label="包含管理员" prop="managerIds">
            <el-col :span="18">
                <tnxel-fetch-tags v-model="model.managerIds" url="/manager/list" text-name="caption"
                    :formatter="toManagerTag" theme="primary" empty="暂无管理员" multi filterable/>
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
        'tnxel-fetch-tags': tnx.components.FetchTags,
    },
    data() {
        return {
            url: '/role/add',
            menu: menu,
            model: {
                name: '',
                remark: '',
            },
        };
    },
    methods: {
        toManagerTag(manager) {
            let text = manager.fullName + ' (' + manager.username;
            if (manager.jobNo) {
                text += '#' + manager.jobNo;
            }
            text += ')';
            return {
                id: manager.id,
                caption: text,
            }
        },
        submit() {
            const vm = this;
            const model = Object.assign({}, vm.model, {
                permissions: this.$refs.permissionTree.getPermissions(),
            });
            app.rpc.post(vm.url, model, function() {
                vm.$refs.form.disable();
                tnx.toast('新增成功', function() {
                    vm.$router.back();
                });
            });
        }
    }
}
</script>
