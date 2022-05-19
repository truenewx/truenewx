<template>
    <div role="alert" class="el-alert" :class="[typeClass, effectClass]">
        <tnxel-icon :value="iconValue" class="el-alert__icon flex-center" :style="iconStyle" @click="iconClick"
            v-if="showIcon"/>
        <div class="el-alert__content" :class="contentClass">
            <div class="el-alert__title" :style="titleStyle">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
import Icon from '../icon/Icon';

export default {
    name: 'TnxelAlert',
    components: {
        'tnxel-icon': Icon,
    },
    props: {
        type: {
            type: String,
            default: () => 'info'
        },
        effect: {
            type: String,
            default: () => 'light'
        },
        showIcon: {
            type: Boolean,
            default: () => true
        },
        icon: String,
        iconSize: {
            type: [Number, String],
            default: 14,
        },
        iconClick: Function,
        titleSize: [Number, String],
        contentClass: String,
    },
    computed: {
        alertType() {
            if (this.type === 'danger') {
                return 'error';
            }
            if (this.type === 'error' || this.type === 'warning' || this.type === 'success') {
                return this.type;
            }
            return 'info';
        },
        typeClass() {
            return 'el-alert--' + this.alertType;
        },
        effectClass() {
            return 'is-' + this.effect;
        },
        iconValue() {
            if (this.icon) {
                return this.icon;
            }
            switch (this.alertType) {
                case 'error':
                    return 'CircleCloseFilled';
                case 'warning':
                    return 'WarningFilled';
                case 'success':
                    return 'CircleCheckFilled';
                default:
                    return 'InfoFilled';
            }
        },
        iconStyle() {
            let style = {};
            if (this.iconSize) {
                let size = this.iconSize;
                if (typeof size === 'number') {
                    size += 'px';
                }
                style.fontSize = size;
                style.width = size;
            }
            if (this.iconClick) {
                style.cursor = 'pointer';
            }
            return style;
        },
        titleStyle() {
            let style = {};
            if (this.titleSize) {
                let size = this.titleSize;
                if (typeof size === 'number') {
                    size += 'px';
                }
                style.fontSize = size;
            }
            return style;
        }
    },
}
</script>

<style>
.el-alert__title {
    text-align: left;
}
</style>
