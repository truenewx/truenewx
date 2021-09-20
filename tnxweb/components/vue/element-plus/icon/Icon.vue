<template>
    <el-icon :class="className" :size="size" :color="color">
        <Loading v-if="type === 'Loading'"/>
        <ArrowDown v-else-if="type === 'ArrowDown'"/>
        <ArrowRight v-else-if="type === 'ArrowRight'"/>
        <Edit v-else-if="type === 'Edit'"/>
        <HomeFilled v-else-if="type === 'HomeFilled'"/>
        <InfoFilled v-else-if="type === 'InfoFilled'"/>
        <OfficeBuilding v-else-if="type === 'OfficeBuilding'"/>
        <Plus v-else-if="type === 'Plus'"/>
        <QuestionFilled v-else-if="type === 'QuestionFilled'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
        <UserFilled v-else-if="type === 'UserFilled'"/>
        <View v-else-if="type === 'View'"/>
    </el-icon>
</template>

<script>
import {
    ArrowDown,
    ArrowRight,
    CircleCloseFilled,
    Edit,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    QuestionFilled,
    Select,
    Setting,
    SuccessFilled,
    UserFilled,
    View,
    WarningFilled
} from '@element-plus/icons';

const components = {
    ArrowDown,
    ArrowRight,
    CircleCloseFilled,
    Edit,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    QuestionFilled,
    Select,
    Setting,
    SuccessFilled,
    UserFilled,
    View,
    WarningFilled,
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
