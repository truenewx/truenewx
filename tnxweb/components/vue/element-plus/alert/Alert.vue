<template>
    <div role="alert" class="el-alert" :class="[typeClass, effectClass]">
        <tnxel-icon class="el-alert__icon flex-center" :type="iconType" :style="iconStyle" v-if="showIcon"/>
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
        iconSize: [Number, String],
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
        iconType() {
            if (this.alertType === 'error') {
                return 'CircleCloseFilled';
            }
            return this.alertType.firstToUpperCase() + 'Filled';
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
    }
}
</script>
