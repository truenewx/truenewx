<template>
    <tnxvw-cells class="weui-cells__group_form" :title="title" :tip="tip">
        <slot></slot>
        <template #tip v-if="$slots.tip">
            <slot name="tip"></slot>
        </template>
    </tnxvw-cells>
</template>

<script>
import Cells from '../cells/Cells';

export default {
    name: 'TnxvwFormItemGroup',
    components: {
        'tnxvw-cells': Cells,
    },
    props: {
        title: String,
        tip: String,
    },
}
</script>

<style>
.weui-cells__group_form:first-child {
    margin-top: 0.5rem;
}

.weui-cells__group_form .weui-cells__title {
    margin-top: 0.5rem;
    padding: 0 1rem;
}

.weui-cells__group_form .weui-cells:before,
.weui-cells__group_form .weui-cells:after {
    border-color: transparent;
}

.weui-cells__group_form .weui-cells__tips {
    padding: 0 1rem;
}
</style>
