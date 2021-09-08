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
import Select from '../select';
import FetchCascader from '../fetch-cascader';

export default {
    name: 'TnxelEnumSelect',
    components: {
        'tnxel-select': Select,
        'tnxel-fetch-cascader': FetchCascader,
    },
    props: {
        id: [Number, String],
        value: String,
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
    data() {
        return {
            model: this.value,
            items: null,
        };
    },
    watch: {
        model(value) {
            this.$emit('input', value);
        },
        value() {
            this.initModel();
        },
        type() {
            this.init();
        },
        subtype() {
            this.init();
        }
    },
    created() {
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
            this.model = this.value;
            if (this.$refs.select && this.$refs.select.isMulti()) {
                return;
            }
            if ((this.model === undefined || this.model === null) && !this.empty && this.items && this.items.length) {
                this.model = this.items[0].key;
            }
        }
    }
}
</script>
