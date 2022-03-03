<template>
    <label class="weui-cell weui-cell_active"
        :class="{'weui-cell_select weui-cell_select-after': getType() === 'select'}">
        <div class="weui-cell__hd">
            <span class="weui-label" :style="labelStyle">{{ label }}</span>
            <div class="weui-cell__desc" v-if="desc">{{ desc }}</div>
        </div>
        <div class="weui-cell__bd" :key="label">
            <slot></slot>
        </div>
    </label>
</template>

<script>
export default {
    name: 'TnxvwFormItem',
    props: {
        label: String,
        desc: String,
        type: String, // select-选择项
        labelWidth: [String, Number],
    },
    computed: {
        labelStyle() {
            let style = {};
            if (this.labelWidth) {
                style.width = typeof this.labelWidth === 'number' ? (this.labelWidth + 'px') : this.labelWidth;
            }
            return style;
        },
    },
    methods: {
        getType() {
            if (this.type) {
                return this.type;
            }
            if (this.$slots?.default) {
                let defaultSlot = this.$slots?.default[0];
                if (defaultSlot?.componentOptions && defaultSlot.componentOptions.tag.endsWith('-picker')) {
                    return 'select';
                }
            }
            return undefined;
        },
    },
}
</script>

<style>
.weui-form__control-area .weui-label {
    margin-right: 0;
}

.weui-form__control-area .weui-cell {
    background-color: var(--weui-BG-2);
}

.weui-form__control-area .weui-cell__bd .weui-flex .weui-btn {
    padding: 0 0 0 0.75rem;
    margin: 0 0 0 0.75rem;
    width: fit-content;
    border-radius: 0;
    border-left: 1px solid var(--weui-FG-3);
    height: 24px;
    display: flex;
    align-items: center;
}

.weui-form__control-area .weui-cell__bd .weui-flex .weui-btn_default {
    background-color: transparent;
}

.weui-form__control-area .weui-cell__bd .weui-flex .weui-btn_default:active {
    background-color: transparent;
}
</style>
