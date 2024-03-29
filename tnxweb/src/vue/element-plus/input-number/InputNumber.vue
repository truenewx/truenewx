<template>
    <div class="tnxel-input-number" :class="containerClassObject">
        <el-input-number ref="input" class="flex-grow-1" :class="{'rounded-end-0': suffix, 'text-start': !controls}"
            v-model="model"
            :min="min" :max="max"
            :controls="controls" controls-position="right"
            :placeholder="placeholderText" :disabled="disabled"
            :step="step || Math.pow(10, -this.scale)" step-strictly
            :precision="scale"
            :value-on-clear="null"
            :size="size"
            @change="onChange"
            @blur="$emit('blur', $event)"/>
        <div class="el-input-group__append" v-if="suffix">{{ suffix }}</div>
    </div>
</template>

<script>
export default {
    name: 'TnxelInputNumber',
    props: {
        modelValue: [Number, String],
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
        size: String,
    },
    emits: ['update:modelValue', 'blur'],
    data() {
        return {
            model: typeof this.modelValue === 'string' ? Number(this.modelValue) : this.modelValue,
            showRequiredError: false,
        }
    },
    computed: {
        placeholderText() {
            if (this.placeholder) {
                return this.placeholder;
            }
            let text = this.disabled ? '未' : '请';
            text += this.controls ? '设置' : '输入';
            return text;
        },
        containerClassObject() {
            let classObject = {
                'is-error': this.showRequiredError,
            };
            if (this.span) {
                classObject['el-col'] = true;
                classObject['el-col-' + this.span] = true;
            }
            return classObject;
        },
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
            this.model = typeof value === 'string' ? Number(value) : value;
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
.tnxel-input-number {
    display: flex;
}

.tnxel-input-number .el-input-number {
    width: auto;
    line-height: 1;
}

.tnxel-input-number .el-input__inner {
    min-width: 50px;
}

.tnxel-input-number .el-input-number.text-start .el-input__inner {
    text-align: left;
}

.tnxel-input-number .el-input-number.is-controls-right .el-input__wrapper {
    padding-left: 8px;
}

.tnxel-input-number .el-input-number .el-input__wrapper .el-input__suffix {
    display: none;
}

.tnxel-input-number .el-input-number.rounded-end-0 .el-input__wrapper {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
}

.tnxel-input-number .el-input-group__append {
    border: 1px solid var(--el-border-color);
    border-left: none;
    line-height: 1;
    border-top-right-radius: var(--el-input-border-radius, var(--el-border-radius-base));
    border-bottom-right-radius: var(--el-input-border-radius, var(--el-border-radius-base));
}
</style>
