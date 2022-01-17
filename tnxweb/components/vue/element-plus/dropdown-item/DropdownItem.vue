<template>
    <el-dropdown-item class="tnxel-dropdown-item">
        <el-tooltip :content="tooltip" :placement="tooltipPlacement" :offset="32" :disabled="!tooltip">
            <div class="flex-v-center" :title="title" v-if="icon">
                <tnxel-icon :type="icon"/>
                <span><slot></slot></span>
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
        title: String,
        tooltip: String,
        tooltipPlacement: {
            type: String,
            default: 'right',
        },
    },
}
</script>

<style>
.tnxel-dropdown-item {
    display: flex;
    align-items: center;
}
</style>
