package org.truenewx.tnxjeex.notice.service.email.send;

import java.util.*;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjeex.notice.model.email.EmailMessage;
import org.truenewx.tnxjeex.notice.model.email.EmailSource;
import org.truenewx.tnxjeex.notice.service.email.provider.EmailProvider;

/**
 * 邮件发送器默认实现
 *
 * @author jianglei
 */
public class EmailSenderImpl implements EmailSender, ContextInitializedBean {

    /**
     * Java邮件发送器
     */
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private EmailSource source;

    private int interval = 1000;
    /**
     * 邮件实体映射集
     */
    private Map<String, EmailProvider> providers = new HashMap<>();
    /**
     * 线程执行器
     */
    private Executor executor;

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, EmailProvider> providerMap = context.getBeansOfType(EmailProvider.class);
        for (EmailProvider provider : providerMap.values()) {
            String type = provider.getType();
            if (type != null) {
                this.providers.put(type, provider);
            }
        }
    }

    /**
     * @param executor 线程执行器，未配置时不采用多线程的方式执行
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public EmailProvider getProvider(String type) {
        return this.providers.get(type);
    }

    @Override
    public void send(String type, Iterable<String> addressees, Map<String, Object> params, Locale locale,
            EmailSendProgress progress) {
        EmailProvider provider = getProvider(type);
        if (provider != null && addressees.iterator().hasNext()) {
            String title = provider.getTitle(params, locale);
            String content = provider.getContent(params, locale);
            List<EmailMessage> messages = new ArrayList<>();
            for (String addressee : addressees) {
                if (StringUtil.isEmail(addressee)) {
                    messages.add(new EmailMessage(addressee, title, content));
                }
            }
            EmailSendCommand command = new EmailSendCommand(this.sender, this.source, messages, this.interval,
                    progress);
            if (this.executor == null) {
                command.run();
            } else {
                this.executor.execute(command);
            }
        }
    }
}
