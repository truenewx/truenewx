package org.truenewx.tnxjee.core.message;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.util.LogUtil;

/**
 * 属性消息来源
 *
 * @author jianglei
 */
public class PropertiesMessageSource extends ReloadableResourceBundleMessageSource
        implements MessagesSource, InitializingBean {

    private static final String PROPERTIES_SUFFIX = ".properties";

    @Autowired
    private Environment environment;
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    @Autowired
    private EnumDictResolver enumDictResolver;
    private Locale[] locales;

    public PropertiesMessageSource() {
    }

    public void setLocales(Locale... locales) {
        this.locales = locales;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<String> set = new LinkedHashSet<>();
        for (String basename : getBasenameSet()) {
            basename = basename.trim();
            if (basename.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
                try {
                    Resource[] resources = this.resourcePatternResolver
                            .getResources(basename + PROPERTIES_SUFFIX);
                    for (Resource resource : resources) {
                        String path = resource.getURI().toString();
                        path = path.substring(0, path.length() - PROPERTIES_SUFFIX.length());
                        // 去掉路径中可能的Locale后缀
                        if (this.locales != null) {
                            for (Locale locale : this.locales) {
                                String suffix = Strings.UNDERLINE + locale.toString();
                                if (path.endsWith(suffix)) {
                                    path = path.substring(0, path.length() - suffix.length());
                                    break;
                                }
                            }
                        }
                        set.add(path);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClass(), e);
                }
            } else {
                set.add(basename);
            }
        }
        setBasenames(set.toArray(new String[set.size()]));
    }

    @Override
    protected String getMessageInternal(String code, Object[] args, Locale locale) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Enum) {
                    args[i] = this.enumDictResolver.getText((Enum<?>) args[i], locale);
                }
            }
        }
        String message = super.getMessageInternal(code, args, locale);
        if (message == null || message.equals(code)) {
            message = this.environment.getProperty(code, code);
        }
        return message;
    }

    @Override
    public Map<String, String> getMessages(Locale locale) {
        Map<String, String> messages = new TreeMap<>();
        Properties properties = getMergedProperties(locale).getProperties();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            messages.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return messages;
    }

    @Override
    public Map<String, String> getMessages(Locale locale, String prefix, boolean resultContainsPrefix) {
        Map<String, String> messages = new TreeMap<>();
        int prefixLength = prefix.length();
        Properties properties = getMergedProperties(locale).getProperties();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                if (!resultContainsPrefix) {
                    key = key.substring(prefixLength);
                }
                messages.put(key, entry.getValue().toString());
            }
        }
        return messages;
    }

}
