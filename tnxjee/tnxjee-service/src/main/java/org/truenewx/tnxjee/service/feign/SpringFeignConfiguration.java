package org.truenewx.tnxjee.service.feign;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.Feign;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class SpringFeignConfiguration extends FeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder springFeignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(this.messageConverters));
    }

    @Bean
    public Feign.Builder feignBuilder(Client client) {
        return Feign.builder()
                .queryMapEncoder(new BeanPropertyQueryMapEncoder())
                .client(client);
    }

}
