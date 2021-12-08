<template>
    <el-dropdown-item class="tnxel-dropdown-item">
        <template v-if="icon">
            <tnxel-icon :type="icon"/>
            <span><slot></slot></span>
        </template>
        <slot v-else></slot>
    </el-dropdown-item>
</template>

<script>
export default {
    name: 'TnxelDropdownItem',
    props: {
        icon: String,
    },
}
</script>

<style>
.tnxel-dropdown-item {
    display: flex;
    align-items: center;
}
</style>
