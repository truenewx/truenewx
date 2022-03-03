<template>
    <div class="weui-msg">
        <div class="weui-msg__icon-area" v-if="$slots.icon || icon">
            <slot name="icon" v-if="$slots.icon"></slot>
            <i class="weui-icon_msg" :class="icon" v-else></i>
        </div>
        <div class="weui-msg__text-area">
            <h2 class="weui-msg__title" v-if="title">{{ title }}</h2>
            <p class="weui-msg__desc" v-if="$slots.desc">
                <slot name="desc"></slot>
            </p>
            <p class="weui-msg__desc" v-else-if="desc">{{ desc }}</p>
        </div>
        <div class="weui-msg__custom-area" v-if="$slots.default">
            <slot></slot>
        </div>
        <div class="weui-msg__opr-area" v-if="$slots.opr">
            <slot name="opr"></slot>
        </div>
        <div class="weui-msg__tips-area" v-if="tip">
            <p class="weui-msg__tips">{{ tip }}</p>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TnxvwMsg',
    props: {
        icon: String,
        title: String,
        desc: String,
        secondaryDesc: String,
        tip: String,
    },
}
</script>

<style>
.weui-msg .weui-icon_msg:not([class*="weui-icon-"]) {
    width: unset;
    height: unset;
    font-size: 4rem;
}
</style>
