<template>
    <el-dropdown-item :disabled="disabled">
        <el-tooltip :content="tooltip" :placement="tooltipPlacement" :offset="32" :disabled="!tooltip">
            <div class="w-100 flex-v-center" :title="title" v-if="icon">
                <tnxel-icon :value="icon" :size="iconSize"/>
                <span class="w-100"><slot></slot></span>
            </div>
            <div class="w-100" :title="title" v-else>
                <slot></slot>
            </div>
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
