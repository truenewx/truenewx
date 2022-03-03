<template>
    <tnxvw-picker
        v-model="model"
        :title="title"
        :placeholder="placeholder"
        :empty="empty"
        :items="items"
        value-name="key"
        text-name="caption"
    />
</template>

<script>
import Picker from '../picker/Picker';

export default {
    name: 'TnxvwEnumPicker',
    components: {
        'tnxvw-picker': Picker,
    },
    props: {
        model: {
            type: [String, Boolean],
            default: null,
        },
        title: String,
        placeholder: String,
        empty: [Boolean, String],
        type: {
            type: String,
            required: true,
        },
        subtype: String,
    },
    data() {
        return {
            items: [],
        }
    },
    watch: {
        model() {
            this.$emit('input', this.model);
        },
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            if (this.type) {
                if (this.type.toLowerCase() === 'boolean') {
                    this.items = [{
                        key: true,
                        caption: true.toText(),
                    }, {
                        key: false,
                        caption: false.toText(),
                    }];
                } else {
                    let vm = this;
                    window.tnx.app.rpc.loadEnumItems(this.type, this.subtype, function(items) {
                        vm.items = items;
                    });
                }
            }
        },
    }
}
</script>
