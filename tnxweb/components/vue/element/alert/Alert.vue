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
        typeClass() {
            return 'el-alert--' + this.type;
        },
        effectClass() {
            return 'is-' + this.effect;
        },
        iconClass() {
            return 'el-icon-' + this.type;
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
