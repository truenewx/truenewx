<template>
    <div class="d-flex"
        :class="{'justify-content-center': align === 'center', 'justify-content-end': align === 'right'}">
        <el-pagination layout="total, sizes, prev, pager, next" :background="background" @current-change="change"
            :page-size="value.pageSize || 0" :page-sizes="[value.pageSize || 0]" popper-class="d-none"
            :current-page="value.pageNo" :total="value.total || 0" v-if="value"/>
    </div>
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
        },
        align: String,
    },
}
</script>
