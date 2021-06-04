<template>
    <el-row class="container-fluid" type="flex" align="middle" justify="space-between">
        <h3>
            <el-link :href="contextPath + '/'" :underline="false">{{ title }}</el-link>
        </h3>
        <el-row type="flex" align="middle" v-if="manager.caption">
            <el-avatar class="mr-2" icon="el-icon-user-solid" :src="manager.headImageUrl" :size="32"/>
            <el-dropdown trigger="click">
                <span class="el-dropdown-link">{{ manager.caption }}
                    <i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item @click.native="info">个人资料</el-dropdown-item>
                    <el-dropdown-item @click.native="password">修改密码</el-dropdown-item>
                    <el-dropdown-item @click.native="logout" divided>登出系统</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </el-row>
    </el-row>
</template>

<script>
import {app, tnx} from '../app.js';
import info from '../pages/self/info.vue';
import password from '../pages/self/password.vue';

export default {
    data() {
        return {
            title: process.env.VUE_APP_TITLE,
            contextPath: process.env.VUE_APP_VIEW_BASE_URL,
            manager: {
                caption: null,
                headImageUrl: null,
            },
        };
    },
    created() {
        const vm = this;
        app.rpc.get('/manager/self/least', manager => {
            vm.manager.caption = manager.caption;
            app.rpc.ensureLogined(function() {
                vm.manager.headImageUrl = manager.headImageUrl;
            }, {
                app: 'fss',
                toLogin: function(loginFormUrl, originalUrl, originalMethod) {
                    return true;
                }
            });
        });
    },
    methods: {
        info() {
            tnx.open(info, {
                opener: this
            });
        },
        password() {
            tnx.open(password, {
                opener: this
            });
        },
        logout() {
            if (process.env.NODE_ENV === 'production') {
                window.location.href = this.contextPath + "/logout";
            } else {
                app.rpc.post("/logout");
            }
        }
    }
}
</script>
