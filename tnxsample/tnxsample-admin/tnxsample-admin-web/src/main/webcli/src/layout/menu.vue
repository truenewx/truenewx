<template>
    <div>
        <div class="p-3 text-center border-bottom" style="line-height: 24px;">
            <i :class="collapsed ? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'" role="button"
                @click="toggleCollapsed"></i>
        </div>
        <el-menu class="border-right-0" :default-active="activePath" :collapse="collapsed" router
            unique-opened>
            <template v-for="(item, itemIndex) in items">
                <el-submenu v-if="item.subs" :key="itemIndex" :index="'' + itemIndex">
                    <template slot="title">
                        <i class="fas" :class="item.icon"></i>
                        <span>{{ item.caption }}</span>
                    </template>
                    <el-menu-item v-for="(sub, subIndex) in item.subs"
                        :key="itemIndex + '-' + subIndex" :index="sub.path">
                        <i class="fas" :class="sub.icon"></i>
                        <span>{{ sub.caption }}</span>
                    </el-menu-item>
                </el-submenu>
                <el-menu-item v-else :key="itemIndex" :index="item.path">
                    <i class="fas" :class="item.icon"></i>
                    <span slot="title">{{ item.caption }}</span>
                </el-menu-item>
            </template>
        </el-menu>
    </div>
</template>

<script>
import menu from '../menu.js';

export default {
    data() {
        return {
            items: null,
            collapsed: false,
        };
    },
    computed: {
        activePath() {
            if (this.$route.path === "/") {
                return undefined;
            }
            let item = menu.findBelongingItem(this.$route.path);
            return item ? item.path : undefined;
        }
    },
    created() {
        const vm = this;
        menu.loadGrantedItems(function(grantedItems) {
            vm.items = grantedItems;
        });
    },
    methods: {
        toggleCollapsed() {
            this.collapsed = !this.collapsed;
        }
    }
}
</script>

<style>
.el-menu:not(.el-menu--collapse) {
    width: 200px;
}

.el-menu--collapse .el-submenu.is-active {
    background-color: #ecf5ff;
}

.el-menu .fa, .el-menu .fas {
    margin-right: 5px;
    width: 20px;
    text-align: center;
    vertical-align: middle;
    color: inherit;
}

.el-menu-item.is-active {
    border-right: 0.3rem solid #409EFF;
    background-color: #ecf5ff;
}
</style>
