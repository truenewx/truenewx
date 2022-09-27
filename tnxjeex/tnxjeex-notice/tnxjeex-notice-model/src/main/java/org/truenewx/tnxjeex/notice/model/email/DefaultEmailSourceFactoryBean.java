package org.truenewx.tnxjeex.notice.model.email;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.truenewx.tnxjee.core.message.MessageResolver;

public class DefaultEmailSourceFactoryBean implements FactoryBean<EmailSource> {

    private String nameCode = "tnxjeex.notice.email.source.name";
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private MessageResolver messageResolver;

    public DefaultEmailSourceFactoryBean() {
    }

    public DefaultEmailSourceFactoryBean(String nameCode) {
        this.nameCode = nameCode;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<?> getObjectType() {
        return EmailSource.class;
    }

    @Override
    public EmailSource getObject() throws Exception {
        String address = this.mailProperties.getUsername();
        String name = this.messageResolver.resolveMessage(this.nameCode);
        String encoding = this.mailProperties.getDefaultEncoding().name();
        return new EmailSource(address, name, encoding);
    }

}
