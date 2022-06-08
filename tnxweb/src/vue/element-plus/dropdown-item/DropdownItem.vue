<template>
    <el-dropdown-item :disabled="disabled">
        <el-tooltip :content="tooltip" :placement="tooltipPlacement" :offset="32" :disabled="!tooltip">
            <div class="w-100 flex-v-center" :title="title" v-if="icon">
                <tnxel-icon :value="icon" :size="iconSize"/>
                <span><slot></slot></span>
            </div>
            <div class="w-100" :title="title" v-else-if="title">
                <slot></slot>
            </div>
            <slot v-else></slot>
        </el-tooltip>
    </el-dropdown-item>
</template>

<script>
export default {
    name: 'TnxelDropdownItem',
    props: {
        icon: String,
        iconSize: Number,
        title: String,
        tooltip: String,
        tooltipPlacement: {
            type: String,
            default: 'right',
        },
        disabled: Boolean,
    },
}
</script>
