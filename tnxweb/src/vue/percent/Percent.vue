<template>
    <span v-if="value !== undefined && value !== null && !isNaN(value)">{{ value * 100 }}%</span>
</template>

<script>
export default {
    name: 'TnxvuePercent',
    props: {
        value: Number,
    }
}
</script>
