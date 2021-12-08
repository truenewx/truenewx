<template>
    <tnxel-icon :type="checked ? trueIcon : falseIcon" class="tnxel-check-icon"
        :class="'text-' + (checked ? trueTheme : falseTheme)" :size="size" @click="click"/>
</template>

<script>
export default {
    name: 'TnxelCheckIcon',
    props: {
        modelValue: Boolean,
        size: Number,
        trueIcon: {
            type: String,
            default: 'CircleCheckFilled',
        },
        falseIcon: {
            type: String,
            default: 'CircleCheck',
        },
        trueTheme: {
            type: String,
            default: 'primary',
        },
        falseTheme: {
            type: String,
            default: 'secondary',
        },
        trigger: {
            type: Boolean,
            default: true,
        }
    },
    emits: ['update:modelValue'],
    data() {
        return {
            checked: this.modelValue,
        }
    },
    watch: {
        modelValue(value) {
            this.checked = value;
        },
        checked(value) {
            this.$emit('update:modelValue', value);
        }
    },
    methods: {
        click() {
            if (this.trigger) {
                this.checked = !this.checked;
            }
        }
    }
}
</script>

<style>
.tnxel-check-icon {
    cursor: pointer;
}
</style>
