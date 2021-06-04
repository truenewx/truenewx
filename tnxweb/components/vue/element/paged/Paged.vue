<template>
    <el-pagination layout="total, sizes, prev, pager, next" :background="background" @current-change="change"
        :page-size="value.pageSize || 0" :page-sizes="[value.pageSize || 0]" popper-class="d-none"
        :current-page="value.pageNo" :total="value.total || 0" v-if="value"/>
</template>

<script>
export default {
    name: 'TnxelPaged',
    props: {
        value: {
            type: Object,
            required: true,
        },
        change: {
            type: Function
        },
        background: {
            type: Boolean,
            default: true,
        }
    },
}
</script>

<style>
.el-pagination .el-pagination__sizes .el-input__suffix {
    display: none;
}

.el-pagination .el-pagination__sizes .el-select .el-input {
    width: 66px;
    margin: 0;
}

.el-pagination .el-pagination__sizes .el-select .el-input .el-input__inner {
    border: none;
    cursor: default;
    padding: 0;
}
</style>
