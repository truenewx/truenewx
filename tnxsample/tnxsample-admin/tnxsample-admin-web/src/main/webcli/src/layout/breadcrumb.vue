<template>
    <div class="d-flex mb-3">
        <el-page-header class="link" @back="back"/>
        <el-breadcrumb class="d-flex align-items-center">
            <el-breadcrumb-item :to="{path: '/'}">
                <i class="fas fa-home"></i> 首页
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item, index) in items" :key="''+index" :to="item.path">
                {{ item.caption }}
            </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
</template>

<script>
import menu from '../menu.js';

export default {
    data() {
        return {};
    },
    computed: {
        items() {
            return menu.getBreadcrumbItems(this.$route.path);
        }
    },
    methods: {
        back() {
            this.$router.back();
        }
    }
}
</script>

<style>
.el-breadcrumb__inner.is-link {
    font-weight: normal;
    color: #007bff;
}

.el-breadcrumb__inner.is-link:hover {
    color: #0056b3;
}

.el-breadcrumb__item:last-child .el-breadcrumb__inner,
.el-breadcrumb__item:last-child .el-breadcrumb__inner:hover {
    font-weight: bold;
}
</style>
