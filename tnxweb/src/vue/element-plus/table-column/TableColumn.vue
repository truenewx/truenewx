<template>
    <el-table-column
        :label="label"
        :prop="prop"
        :header-align="headerAlign || align"
        :align="align"
        :width="width">
        <template #header v-if="required">
            <span class="text-danger" v-if="required">* </span>{{ label }}
        </template>
        <template #default="scope">
            <slot :$index="scope.$index" :row="scope.row" :column="scope.column"></slot>
        </template>
    </el-table-column>
</template>

<script>
export default {
    name: 'TnxelTableColumn',
    props: {
        label: String,
        prop: String,
        headerAlign: String,
        align: {
            type: String,
            default: 'center',
        },
        width: [String, Number],
        required: Boolean,
    },
}
</script>
