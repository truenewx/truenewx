<template>
    <div>
        <p>Component: Hello World {{param}}</p>
    </div>
</template>

<script>
    export default {
        props: ['param', 'opener'],
        data () {
            return {
                abc: 'abc',
            };
        },
        methods: {
            dialog () {
                return {
                    title: '组件',
                    type: 'confirm',
                    click: this.toSubmit
                }
            },
            toSubmit (yes, close) {
                console.info('this.opener: ' + this.opener);
                console.info(this.abc + ': ' + yes);
            }
        }
    }
</script>
