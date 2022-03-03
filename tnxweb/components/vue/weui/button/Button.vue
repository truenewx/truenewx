<template>
    <button role="button" :class="className" :disabled="disabled" @click="click">
        <span class="weui-primary-loading" v-if="loading">
            <i class="weui-primary-loading__dot"></i>
        </span>
        <slot></slot>
    </button>
</template>

<script>
export default {
    name: "TnxvwButton",
    props: {
        type: {
            type: String,
            default: 'default',
        },
        loading: Boolean,
        disabled: Boolean,
        size: {
            type: String,
            default: 'default', // cell-占整行，default-默认，mini-迷你
        },
    },
    computed: {
        className() {
            let className = 'weui-btn weui-wa-hotarea weui-btn_' + this.type;
            if (this.size !== 'default') {
                className += ' weui-btn_' + this.size;
            }
            if (this.disabled) {
                className += ' weui-btn_disabled';
            }
            return className;
        }
    },
    methods: {
        click(event) {
            if (!this.disabled) {
                this.$emit('click', event);
            }
        }
    }
}
</script>
