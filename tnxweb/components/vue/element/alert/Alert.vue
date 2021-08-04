<template>
    <div role="alert" class="el-alert" :class="[typeClass, effectClass]">
        <i class="el-alert__icon" :class="iconClass" :style="iconStyle" v-if="showIcon"></i>
        <div class="el-alert__content" :class="contentClass">
            <div class="el-alert__title">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TnxelAlert',
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
        iconSize: [Number, String],
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
        iconClass() {
            return 'el-icon-' + this.alertType;
        },
        iconStyle() {
            let style = {};
            if (this.iconSize) {
                let iconSize = this.iconSize;
                if (typeof iconSize === 'number') {
                    iconSize += 'px';
                }
                style.fontSize = iconSize;
                style.width = iconSize;
            }
            return style;
        }
    }
}
</script>
