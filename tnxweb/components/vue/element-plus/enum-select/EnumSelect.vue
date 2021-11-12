<template>
    <tnxel-fetch-cascader v-model="model" url="/api/meta/enums" value-name="key" text-name="caption"
        index-name="searchIndex" :disabled="disabled" :empty="empty" :filterable="filterable" :theme="theme"
        :params="{
            type: type,
            subtype:subtype,
            grouped: true,
        }" v-if="items && grouped"/>
    <tnxel-select ref="select" v-model="model" :id="id" :selector="selector" :items="items"
        value-name="key" text-name="caption" index-name="searchIndex"
        :default-value="defaultValue" :empty="empty" :empty-value="emptyValue"
        :placeholder="placeholder" :disabled="disabled" :filterable="filterable"
        :theme="theme" :size="size" :tag-click="tagClick" :change="change" v-else-if="items"/>
</template>

<script>
import Select, {isMultiSelector} from '../select/Select';
import FetchCascader from '../fetch-cascader/FetchCascader';

export default {
    name: 'TnxelEnumSelect',
    components: {
        'tnxel-select': Select,
        'tnxel-fetch-cascader': FetchCascader,
    },
    props: {
        id: [Number, String],
        modelValue: [String, Number, Boolean, Array],
        selector: String,
        type: {
            type: String,
            required: true,
        },
        subtype: String,
        defaultValue: String,
        empty: {
            type: [Boolean, String],
            default: false,
        },
        emptyValue: {
            type: [String, Boolean, Number],
            default: () => null,
        },
        placeholder: String,
        disabled: Boolean,
        tagClick: Function,
        change: Function,
        grouped: {
            type: Boolean,
            default: false,
        },
        filterable: Boolean,
        theme: String,
        size: String,
    },
    emits: ['update:modelValue'],
    data() {
        return {
            model: this.modelValue,
            items: null,
        };
    },
    watch: {
        model(value) {
            this.$emit('update:modelValue', value);
        },
        modelValue() {
            this.initModel();
        },
        type() {
            this.init();
        },
        subtype() {
            this.init();
        }
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            if (typeof this.type === 'string') {
                if (this.type.toLowerCase() === 'boolean') {
                    this.items = [{
                        key: true,
                        caption: true.toText(),
                    }, {
                        key: false,
                        caption: false.toText(),
                    }];
                    this.initModel();
                } else {
                    let vm = this;
                    window.tnx.app.rpc.loadEnumItems(this.type, this.subtype, function(items) {
                        vm.items = items;
                        vm.initModel();
                    });
                }
            }
        },
        initModel() {
            let oldModel = this.model;
            this.model = this.modelValue;
            if (isMultiSelector(this.selector)) {
                return;
            }
            if ((this.model === undefined || this.model === null) && !this.empty && this.items && this.items.length) {
                let item = this.items[0];
                this.model = item.key;
                if (this.model !== oldModel && this.change) {
                    this.change(item);
                }
            }
        },
    }
}
</script>
