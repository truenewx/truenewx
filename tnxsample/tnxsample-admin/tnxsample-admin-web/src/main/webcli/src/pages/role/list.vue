<template>
    <div>
        <el-row class="mb-3">
            <el-col :span="4">
                <el-input placeholder="输入名称的关键字" prefix-icon="el-icon-search"
                    v-model="params.name" clearable @clear="query">
                    <el-button slot="append" @click="query">查询</el-button>
                </el-input>
            </el-col>
        </el-row>
        <div class="d-flex justify-content-between mb-3">
            <el-button type="primary" @click="toAdd" v-if="addable">新增角色</el-button>
            <el-alert type="info" class="m-0" title="角色用于向管理员授予操作权限" :closable="false" show-icon></el-alert>
        </div>
        <el-table :data="records" :empty-text="emptyRecordText" border stripe>
            <el-table-column prop="name" label="名称" min-width="160px" width="160px"/>
            <el-table-column label="操作权限" class-name="tnxel-table_tags nowrap" min-width="40%">
                <template slot-scope="scope">
                    <el-tag type="success" v-for="permission in scope.row.permissions"
                        :key="permission">
                        {{ permission }}
                    </el-tag>
                    <span class="text-muted"
                        v-if="scope.row.permissions.length === 0">&lt;无&gt;</span>
                </template>
            </el-table-column>
            <el-table-column label="管理员" class-name="tnxel-table_tags nowrap" min-width="40%">
                <template slot-scope="scope">
                    <template v-if="scope.row.managerNum > 0">
                        <span class="mr-2">共{{ scope.row.managerNum }}人</span>
                        <el-tag v-for="manager in scope.row.managers" :key="manager.id">
                            {{ manager.fullName }}
                        </el-tag>
                    </template>
                    <span class="text-muted" v-else>&lt;无&gt;</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" class-name="tnxel-table_tags" min-width="100px"
                width="100px" header-align="center" align="center">
                <template slot-scope="scope">
                    <router-link :to="'/role/' + scope.row.id + '/update'"
                        class="tnxel-table_tag" v-if="updatable">修改</router-link>
                    <a href="javascript:void(0)" class="tnxel-table_tag"
                        @click="toDelete(scope.$index)" v-if="deletable">删除</a>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import {app, tnx} from "../../app";
import menu from '../../menu.js';

export default {
    data() {
        return {
            addable: false,
            updatable: false,
            deletable: false,
            params: {},
            records: null,
        };
    },
    computed: {
        emptyRecordText() {
            return this.records === null ? '加载中...' : '暂无数据';
        }
    },
    created() {
        const vm = this;
        menu.loadGrantedItems(() => {
            vm.addable = menu.isGranted('/role/add');
            vm.updatable = menu.isGranted('/role/*/update');
            vm.deletable = menu.isGranted('/role/*/delete');
        });
        this.query();
    },
    methods: {
        toAdd() {
            this.$router.push('/role/add');
        },
        toDelete(index) {
            const roles = this.records;
            const role = roles[index];
            let message = '确定要删除吗？';
            if (role.managerNum > 0) {
                message = '删除角色将使得其下的所有管理员（共' + role.managerNum + '人）失去该角色及其操作权限，' + message;
            }
            tnx.confirm(message, yes => {
                if (yes) {
                    app.rpc.post('/role/' + role.id + '/delete', () => {
                        roles.splice(index, 1);
                    });
                }
            });
        },
        query() {
            const vm = this;
            app.rpc.get('/role/list', this.params, function(records) {
                for (let record of records) {
                    if (record.permissions) {
                        for (let i = 0; i < record.permissions.length; i++) {
                            const item = menu.getItemByPermission(record.permissions[i]);
                            if (item) {
                                record.permissions[i] = item.caption;
                            }
                        }
                    }
                }
                vm.records = records;
            });
        }
    }
}
</script>

<style>
</style>
