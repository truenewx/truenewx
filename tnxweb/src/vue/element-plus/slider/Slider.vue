<template>
    <div class="tnxel-slider">
        <el-slider class="flex-grow-1" v-model="index" :max="max" :format-tooltip="format" :disabled="disabled"
            show-stops @input="input" @change="change"/>
        <div class="ms-3 text-center text-regular" :style="{visibility: captionVisibility, width: captionMaxWidth}">
            {{ caption }}
        </div>
    </div>
</template>

<script>

export default {
    name: 'TnxelSlider',
    props: {
        modelValue: Number,
        items: Array,
        itemUnit: {
            type: String,
            default: '',
        },
        disabled: Boolean,
    },
    emits: ['update:modelValue'],
    data() {
        let index = this.indexOf(this.modelValue);
        if (index < 0) {
            index = 0;
        }
        return {
            index: index,
            captionVisibility: this.disabled ? 'hidden' : 'visible',
        }
    },
    computed: {
        max() {
            return this.items.length - 1;
        },
        caption() {
            return this.format(this.index);
        },
        captionMaxWidth() {
            let maxLength = 0;
            for (let i = 0; i < this.items.length; i++) {
                let caption = this.format(i);
                if (caption.length > maxLength) {
                    maxLength = caption.length;
                }
            }
            return (maxLength * 14) + 'px';
        },
    },
    watch: {
        modelValue(value) {
            return this.indexOf(value);
        },
        index(index) {
            let value = this.items[index];
            this.$emit('update:modelValue', value);
        },
        disabled() {
            this.captionVisibility = this.disabled ? 'hidden' : 'visible';
        },
    },
    methods: {
        indexOf(value) {
            return this.items.indexOf(value);
        },
        format(index) {
            return this.items[index] + this.itemUnit;
        },
        input(index) {
            this.captionVisibility = 'hidden';
        },
        change(index) {
            this.captionVisibility = 'visible';
        }
    }
}
</script>

<style>
.tnxel-slider {
    display: flex;
    align-items: center;
    padding-left: 10px;
}
</style>
