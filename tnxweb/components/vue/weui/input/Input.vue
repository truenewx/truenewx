<template>
    <div v-if="type === 'textarea'">
        <textarea ref="textarea"
            class="weui-textarea"
            :rows="rows"
            :placeholder="placeholder"
            :maxlength="maxLength"
            @input="onInput"
            @blur="$emit('blur', $event)"
        ></textarea>
        <div role="option" aria-live="polite" class="weui-textarea-counter" v-if="showLengthLimit">
            <span>{{ (model || '').length }}</span>/{{ maxLength }}
        </div>
    </div>
    <input ref="input"
        class="weui-input"
        :type="type"
        :placeholder="placeholder"
        :maxlength="maxLength"
        @input="onInput"
        @blur="$emit('blur', $event)"
        v-else/>
</template>

<script>
export default {
    name: 'TnxvwInput',
    props: {
        model: String,
        type: {
            type: String,
            default: 'text', // text、textarea和其它原生 input 的 type 值，如：password、number、file等
        },
        placeholder: {
            type: String,
            default: '请输入',
        },
        rows: {
            type: Number,
            default: 3,
        },
        maxLength: Number,
        showLengthLimit: Boolean,
    },
    methods: {
        onInput(event) {
            this.$emit('input', event.target.value);
        },
        focus() {
            if (this.$refs.input) {
                this.$refs.input.focus();
            } else if (this.$refs.textarea) {
                this.$refs.textarea.focus();
            }
        },
    },
}
</script>
