<template>
    <span v-if="text">{{ prepend }}{{ text }}{{ append }}</span>
    <span v-else-if="emptyText">{{ emptyText }}</span>
</template>

<script>
export default {
    name: 'TnxvueText',
    props: {
        value: [String, Number, Boolean, Function],
        prepend: String,
        append: String,
        emptyText: {
            type: String,
            default: '-',
        },
    },
    computed: {
        text() {
            if (this.value !== undefined && this.value !== null) {
                if (typeof this.value === 'function') {
                    return this.value();
                }
                if (typeof this.value === 'boolean') {
                    return this.value.toText();
                }
                return this.value;
            }
            return '';
        }
    }
}
</script>
