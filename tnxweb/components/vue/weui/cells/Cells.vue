<template>
    <div class="weui-cells__group">
        <div class="weui-cells__title" v-if="title">{{ title }}</div>
        <div class="weui-cells">
            <slot></slot>
        </div>
        <div class="weui-cells__tips" v-if="$slots.tip || tip">
            <slot name="tip" v-if="$slots.tip"></slot>
            <template v-else-if="tip">{{ tip }}</template>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TnxvwCells',
    props: {
        title: String,
        tip: String,
    },
}
</script>
