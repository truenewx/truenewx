<template>
    <el-icon :class="{'is-loading': type === 'Loading'}" :size="size" :color="color">
        <Loading v-if="type === 'Loading'"/>
        <ArrowRight v-else-if="type === 'ArrowRight'"/>
        <Edit v-else-if="type === 'Edit'"/>
        <OfficeBuilding v-else-if="type === 'OfficeBuilding'"/>
        <Plus v-else-if="type === 'Plus'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
    </el-icon>
</template>

<script>
import {ArrowRight, Edit, Loading, OfficeBuilding, Plus, Select, Setting} from '@element-plus/icons';

export default {
    name: 'TnxelIcon',
    components: {
        ArrowRight, Edit, Loading, OfficeBuilding, Plus, Select, Setting
    },
    props: {
        type: String,
        color: {},
        size: Number,
    },
}
</script>
