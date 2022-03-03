<template>
    <div class="tnxvw-form-item__error">
        <slot></slot>
    </div>
</template>

<script>
export default {
    name: 'TnxvwFormItemError',
    methods: {},
}
</script>

<style>
.tnxvw-form-item__error {
    color: var(--weui-RED);
    font-size: 14px;
    line-height: 1.4;
}

.weui-form__control-area .weui-cell + .tnxvw-form-item__error {
    margin-top: 0;
    position: relative;
    top: -8px;
    padding: 0 1rem;
}
</style>
