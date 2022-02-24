<template>
    <div class="weui-msg">
        <div class="weui-msg__icon-area" v-if="icon || $slots.icon">
            <i class="weui-icon_msg" :class="icon" v-if="icon"></i>
            <slot name="icon"></slot>
        </div>
        <div class="weui-msg__text-area">
            <h2 class="weui-msg__title" v-if="title">{{ title }}</h2>
            <p class="weui-msg__desc" v-if="desc">{{ desc }}</p>
            <p class="weui-msg__desc-primary" v-if="secondaryDesc">{{ secondaryDesc }}</p>
        </div>
        <div class="weui-msg__opr-area" v-if="$slots.default">
            <slot></slot>
        </div>
        <div class="weui-msg__tips-area" v-if="tip">
            <p class="weui-msg__tips">{{ tip }}</p>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TnxweMsg',
    props: {
        icon: String,
        title: String,
        desc: String,
        secondaryDesc: String,
        tip: String,
    },
    data() {
        return {}
    },
    mounted() {
    },
    methods: {}
}
</script>

<style>
.weui-msg .weui-icon_msg {
    width: unset;
    height: unset;
    font-size: 4rem;
}
</style>
