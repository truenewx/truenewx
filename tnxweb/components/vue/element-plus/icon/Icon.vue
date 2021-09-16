<template>
    <el-icon :class="className" :size="size" :color="color">
        <Loading v-if="type === 'Loading'"/>
        <ArrowDown v-else-if="type === 'ArrowDown'"/>
        <ArrowRight v-else-if="type === 'ArrowRight'"/>
        <Edit v-else-if="type === 'Edit'"/>
        <HomeFilled v-else-if="type === 'HomeFilled'"/>
        <OfficeBuilding v-else-if="type === 'OfficeBuilding'"/>
        <Plus v-else-if="type === 'Plus'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
        <UserFilled v-else-if="type === 'UserFilled'"/>
    </el-icon>
</template>

<script>
import {
    ArrowDown,
    ArrowRight,
    Edit,
    HomeFilled,
    Loading,
    OfficeBuilding,
    Plus,
    Select,
    Setting,
    UserFilled
} from '@element-plus/icons';

const components = {
    ArrowDown, ArrowRight, Edit, HomeFilled, Loading, OfficeBuilding, Plus, Select, Setting, UserFilled
};

export default {
    name: 'TnxelIcon',
    components: components,
    props: {
        type: String,
        color: {},
        size: Number,
    },
    computed: {
        className() {
            if (this.type && components[this.type]) {
                if (this.type === 'Loading') {
                    return 'is-loading';
                }
            } else {
                return this.type;
            }
            return undefined;
        }
    }
}
</script>
