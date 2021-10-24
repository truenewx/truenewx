<template>
    <el-icon :class="className" :size="size" :color="color">
        <Loading v-if="type === 'Loading'"/>
        <ArrowDown v-else-if="type === 'ArrowDown'"/>
        <ArrowRight v-else-if="type === 'ArrowRight'"/>
        <Bottom v-else-if="type === 'Bottom'"/>
        <CircleCheckFilled v-else-if="type === 'CircleCheckFilled'"/>
        <CircleCloseFilled v-else-if="type === 'CircleCloseFilled'"/>
        <Close v-else-if="type === 'Close'"/>
        <CloseBold v-else-if="type === 'CloseBold'"/>
        <Edit v-else-if="type === 'Edit'"/>
        <HomeFilled v-else-if="type === 'HomeFilled'"/>
        <InfoFilled v-else-if="type === 'InfoFilled'"/>
        <OfficeBuilding v-else-if="type === 'OfficeBuilding'"/>
        <Plus v-else-if="type === 'Plus'"/>
        <QuestionFilled v-else-if="type === 'QuestionFilled'"/>
        <Search v-else-if="type === 'Search'"/>
        <Select v-else-if="type === 'Select'"/>
        <Setting v-else-if="type === 'Setting'"/>
        <Top v-else-if="type === 'Top'"/>
        <UserFilled v-else-if="type === 'UserFilled'"/>
        <View v-else-if="type === 'View'"/>
        <WarningFilled v-else-if="type === 'WarningFilled'"/>
    </el-icon>
</template>

<script>
import {
    ArrowDown,
    ArrowRight,
    Bottom,
    CircleCheckFilled,
    CircleCloseFilled,
    Close,
    CloseBold,
    Edit,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    QuestionFilled,
    Search,
    Select,
    Setting,
    SuccessFilled,
    Top,
    UserFilled,
    View,
    WarningFilled
} from '@element-plus/icons';

const components = {
    ArrowDown,
    ArrowRight,
    Bottom,
    CircleCheckFilled,
    CircleCloseFilled,
    Close,
    CloseBold,
    Edit,
    HomeFilled,
    InfoFilled,
    Loading,
    OfficeBuilding,
    Plus,
    QuestionFilled,
    Search,
    Select,
    Setting,
    SuccessFilled,
    Top,
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
                let className = 'tnxel-icon-' + this.type;
                if (this.type === 'Loading') {
                    className += ' is-loading';
                }
                return className;
            } else {
                return this.type;
            }
        }
    }
}
</script>
