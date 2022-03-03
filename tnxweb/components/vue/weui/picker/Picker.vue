<template>
    <div class="weui-select" @click="showPicker">
        <span v-if="currentText">{{ currentText }}</span>
        <span class="text-placeholder" v-else-if="placeholder">{{ placeholder }}</span>
    </div>
</template>

<script>
import FormItem from "../form-item/FormItem";

const defaultPlaceholder = '请选择';

export default {
    name: 'TnxvwPicker',
    props: {
        model: {
            type: [String, Number, Boolean],
            default: null,
        },
        title: String,
        placeholder: {
            type: String,
            default: defaultPlaceholder,
        },
        empty: [Boolean, String],
        items: {
            type: Array,
            default() {
                return [];
            }
        },
        valueName: {
            type: String,
            default: 'value',
        },
        textName: {
            type: String,
            default: 'text',
        },
    },
    data() {
        return {}
    },
    computed: {
        formItem() {
            let formItem = this.$parent;
            while (formItem) {
                if (formItem.$vnode.tag.endsWith('-' + FormItem.name)) {
                    return formItem;
                } else {
                    formItem = formItem.$parent;
                }
            }
            return undefined;
        },
        currentText() {
            if (this.model && this.items?.length) {
                for (let item of this.items) {
                    if (item[this.valueName] === this.model) {
                        return item[this.textName];
                    }
                }
            }
            return undefined;
        },
    },
    watch: {
        model() {
            this.$emit('input', this.model);
        },
        items() {
            this.initModel();
        },
    },
    mounted() {
        this.initModel();
    },
    methods: {
        initModel() {
            if (this.empty !== true && this.model === null && this.items?.length) {
                this.model = this.items[0][this.valueName];
            }
        },
        showPicker() {
            let items = [];
            if (this.items) {
                for (let item of this.items) {
                    items.push({
                        label: item[this.textName],
                        value: item[this.valueName],
                        disabled: item.disabled,
                    });
                }
            }
            let defaultValue = this.model || items[0]?.value;
            let vm = this;
            window.weui.picker(items, {
                title: this.getTitle(),
                defaultValue: defaultValue ? [defaultValue] : undefined,
                onConfirm: function(result) {
                    let item = result[0];
                    vm.model = item.value;
                },
            });
        },
        getTitle() {
            if (this.title) {
                return this.title;
            }
            if (this.formItem?.label) {
                return defaultPlaceholder + this.formItem.label;
            }
            return undefined;
        },
    }
}
</script>
