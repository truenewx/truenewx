<template>
    <el-col class="tnxel-input-number-group" :class="{'is-error': showRequiredError}" :span="span">
        <el-input-number ref="input" class="flex-grow-1" :class="{'rounded-right-0': suffix}" v-model.trim="model"
            :min="min" :max="max" controls-position="right" :placeholder="placeholder" :disabled="disabled"
            :controls="controls" :step="step || Math.pow(10, -this.scale)" :precision="scale" step-strictly
            @change="onChange"/>
        <div class="el-input-group__append" v-if="suffix">{{ suffix }}</div>
    </el-col>
</template>

<script>
export default {
    name: 'TnxelInputNumber',
    props: {
        value: Number,
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
    data() {
        return {
            model: this.value,
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
            this.$emit('input', value);
        },
        value(value) {
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
