<template>
    <div class="tnxel-pagination-container"
        :class="{'justify-content-center': align === 'center', 'justify-content-end': align === 'right'}" v-if="model">
        <slot :paged="value" :change="change" :background="background">
            <el-pagination layout="total, sizes, prev, pager, next" :background="background" @current-change="change"
                v-model:page-size="model.pageSize" :page-sizes="[model.pageSize || 0]" popper-class="d-none"
                v-model:current-page="model.pageNo" :total="model.total || 0"/>
        </slot>
    </div>
</template>

<script>
export default {
    name: 'TnxelPaged',
    props: {
        value: Object,
        change: {
            type: Function
        },
        background: {
            type: Boolean,
            default: true,
        },
        align: String,
    },
    data() {
        return {
            model: this.value,
        }
    },
    watch: {
        value() {
            this.model = this.value;
        }
    }
}
</script>

<style>
.tnxel-pagination-container {
    display: flex;
    padding: 0.5rem 0;
}

.tnxel-pagination-container .el-pagination {
    padding: 0;
}

.tnxel-pagination-container .el-pagination .el-pagination__sizes .el-input__suffix {
    display: none;
}

.tnxel-pagination-container .el-pagination .el-pagination__sizes .el-select .el-input {
    width: 66px;
    margin: 0;
}

.tnxel-pagination-container .el-pagination .el-pagination__sizes .el-select .el-input .el-input__inner {
    border: none;
    cursor: default;
    padding: 0;
}
</style>
