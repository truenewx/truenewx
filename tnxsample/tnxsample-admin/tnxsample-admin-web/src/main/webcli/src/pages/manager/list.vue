<template>
    <div>
        <el-row class="mb-3">
            <el-col :span="6">
                <el-input placeholder="输入工号/姓名/用户名的关键字" prefix-icon="el-icon-search"
                    v-model="params.keyword" clearable @clear="query">
                    <el-button slot="append" @click="query">查询</el-button>
                </el-input>
            </el-col>
        </el-row>
        <div class="d-flex justify-content-between mb-3">
            <el-button type="primary" @click="toAdd" v-if="addable">新增管理员</el-button>
            <el-alert type="info" class="m-0" title="管理员是用于管理系统的账号" :closable="false" show-icon></el-alert>
        </div>
        <el-table :data="records" :empty-text="emptyRecordText" border stripe>
            <el-table-column prop="jobNo" label="工号" min-width="120px"/>
            <el-table-column prop="fullName" label="姓名" min-width="120px"/>
            <el-table-column prop="username" label="用户名" min-width="120px"/>
            <el-table-column label="所属角色" class-name="tnxel-table_tags nowrap" min-width="240px">
                <template slot-scope="scope">
                    <template v-if="scope.row.roles.length > 0">
                        <el-tag type="warning" v-for="role in scope.row.roles" :key="role.id">
                            {{ role.name }}
                        </el-tag>
                    </template>
                    <span class="text-muted" v-else>&lt;无&gt;</span>
                </template>
            </el-table-column>
            <el-table-column label="状态" min-width="60px" header-align="center" align="center">
                <template slot-scope="scope">
                    <el-tooltip content="已禁用，点击启用" placement="top" v-if="scope.row.disabled">
                        <i class="el-icon-remove-outline link"
                            @click="updateDisabled(scope.$index,false)"></i>
                    </el-tooltip>
                    <el-tooltip content="正常，点击禁用" placement="top" v-else>
                        <i class="el-icon-success link"
                            @click="updateDisabled(scope.$index,true)"></i>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column label="操作" class-name="tnxel-table_tags" min-width="120px"
                header-align="center" align="center">
                <template slot-scope="scope">
                    <router-link :to="'/manager/' + scope.row.id + '/update'"
                        class="tnxel-table_tag" v-if="updatable">
                        <span>修改</span>
                    </router-link>
                    <a class="tnxel-table_tag" href="javascript:void(0)"
                        @click="toResetPassword(scope.$index)">重置密码</a>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination layout="total, prev, pager, next" background @current-change="query"
            :total="paged.total" :page-size="paged.pageSize" :current-page="paged.pageNo"/>
    </div>
</template>

<script>
import menu from "../../menu";
import {app, tnx} from "../../app";
import password from "./password";

export default {
    data() {
        return {
            addable: false,
            updatable: false,
            params: {},
            records: null,
            paged: {},
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
            vm.addable = menu.isGranted('/manager/add');
            vm.updatable = menu.isGranted('/manager/*/update');
            vm.deletable = menu.isGranted('/manager/*/delete');
        });
        this.query();
    },
    methods: {
        toAdd() {
            this.$router.push('/manager/add');
        },
        updateDisabled(index, disabled) {
            const records = this.records;
            const managerId = records[index].id;
            app.rpc.post('/manager/' + managerId + '/update-disabled', () => {
                records[index].disabled = disabled;
            }, {
                params: {
                    disabled: disabled
                }
            });
        },
        toResetPassword(index) {
            tnx.open(password, {
                id: this.records[index].id
            });
        },
        query(pageNo) {
            if (typeof pageNo === 'number') {
                this.params.pageNo = pageNo;
            } else {
                delete this.params.pageNo;
            }
            const vm = this;
            app.rpc.get('/manager/list', this.params, function(result) {
                vm.records = result.records;
                vm.paged = result.paged;
            });
        }
    }
}
</script>
