<template>
    <div class="container">
        <div class="text-center">Copyright © 2020 truenewx 版权所有</div>
        <div class="text-right text-muted">v{{version}}</div>
    </div>
</template>

<script>
import config from '../../package.json';

export default {
    data () {
        return {
            version: config.version,
        };
    }
}
</script>
