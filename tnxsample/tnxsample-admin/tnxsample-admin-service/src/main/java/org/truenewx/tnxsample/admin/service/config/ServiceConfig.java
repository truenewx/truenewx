package org.truenewx.tnxsample.admin.service.config;

import java.util.concurrent.Executor;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.truenewx.tnxjee.core.util.concurrent.DefaultThreadPoolExecutor;

/**
 * 服务层配置
 *
 * @author jianglei
 */
@Configuration
@EnableFeignClients
public class ServiceConfig {

    @Bean(name = "defaultExecutor")
    @Primary
    public Executor defaultExecutor() {
        return new DefaultThreadPoolExecutor(4, 16);
    }

}
