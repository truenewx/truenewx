package org.truenewx.tnxjee.repo.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.truenewx.tnxjee.repo.mongo.support.MongoAccessTemplate;

/**
 * MongoDB数据层配置
 *
 * @author jianglei
 */
@Configuration
public class MongoDataConfiguration {

    protected String getSchema() {
        return null;
    }

    @Bean
    public MongoAccessTemplate mongoAccessTemplate(MongoTemplate mongoTemplate) {
        String schema = getSchema();
        if (schema == null) {
            return new MongoAccessTemplate(mongoTemplate);
        } else {
            return new MongoAccessTemplate(schema, mongoTemplate);
        }
    }

}
