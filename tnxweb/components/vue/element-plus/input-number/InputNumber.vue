<template>
    <el-col class="tnxel-input-number" :class="{'is-error': showRequiredError}" :span="span">
        <el-input-number ref="input" class="flex-grow-1" :class="{'rounded-right-0': suffix}"
            v-model="model"
            :min="min" :max="max"
            :controls="controls" controls-position="right"
            :placeholder="disabled ? '未设置' : placeholder" :disabled="disabled"
            :step="step || Math.pow(10, -this.scale)" step-strictly
            :precision="scale"
            @change="onChange"
            @blur="$emit('blur', $event)"/>
        <div class="el-input-group__append" v-if="suffix">{{ suffix }}</div>
    </el-col>
</template>

<script>
export default {
    name: 'TnxelInputNumber',
    props: {
        modelValue: Number,
        span: Number,
        min: Number,
        max: Number,
        placeholder: {
            type: String,
            default: () => '请设置',
        },
        disabled: Boolean,
        append: String,
        controls: {
            type: Boolean,
            default: true,
        },
        step: Number,
        scale: {
            type: Number,
            default: 0,
        },
        required: Boolean,
    },
    emits: ['update:modelValue', 'blur'],
    data() {
        return {
            model: this.modelValue,
            showRequiredError: false,
        }
    },
    computed: {
        suffix() {
            if (this.append) {
                return this.append;
            }
            if (this.$slots && this.$slots.append && this.$slots.append.length) {
                return this.$slots.append[0].text;
            }
            return null;
        }
    },
    watch: {
        model(value) {
            this.$emit('update:modelValue', value);
        },
        modelValue(value) {
            this.model = value;
        }
    },
    methods: {
        validateRequired(focusError) {
            if (this.required) {
                this.showRequiredError = this.model === undefined || this.model === null || this.model === '';
                if (this.showRequiredError && focusError) {
                    this.focus();
                }
                return !this.showRequiredError;
            }
            return true;
        },
        focus() {
            this.$refs.input.focus();
        },
        onChange() {
            this.validateRequired(false);
        },
    }
}
</script>

<style>
.tnxel-input-number .el-input-number {
    width: auto;
}

.tnxel-input-number .el-input-number.is-controls-right .el-input__wrapper {
    padding-left: 12px;
}

.tnxel-input-number .el-input-number .el-input__wrapper .el-input__suffix {
    display: none;
}
</style>
