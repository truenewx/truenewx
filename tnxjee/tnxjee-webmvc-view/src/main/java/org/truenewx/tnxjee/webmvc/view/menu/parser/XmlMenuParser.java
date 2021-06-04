package org.truenewx.tnxjee.webmvc.view.menu.parser;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.PrimitiveClassLoader;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.algorithm.AlgoParseString;
import org.truenewx.tnxjee.webmvc.view.menu.model.Menu;
import org.truenewx.tnxjee.webmvc.view.menu.model.MenuElement;
import org.truenewx.tnxjee.webmvc.view.menu.model.MenuItem;

/**
 * XML配置文件的菜单解析器
 *
 * @author jianglei
 */
@Component
public class XmlMenuParser implements MenuParser, ResourceLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.classLoader = new PrimitiveClassLoader(resourceLoader.getClassLoader(), true);
    }

    @Override
    public Resource getDefaultLocation() {
        return new ClassPathResource("META-INF/menu.xml");
    }

    @Override
    public Menu parse(Resource resource) {
        if (resource != null && resource.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(resource.getInputStream());
                Element element = doc.getRootElement();
                Menu menu = new Menu(element.attributeValue("user-type"));
                parseElementCommon(menu, element, null);
                menu.setItems(getItems(element, menu.getOptions()));
                return menu;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * 解析菜单元素的通用属性
     */
    private void parseElementCommon(MenuElement menuElement, Element domElement,
            Map<String, Object> parentOptions) throws Exception {
        menuElement.setOptions(getOptions(domElement, parentOptions));

        String caption = domElement.attributeValue("caption");
        if (caption != null) {
            menuElement.setCaption(caption);
        }
        // 加入而不是重置
        menuElement.getCaptions().putAll(getCaptions(domElement));

        String desc = domElement.attributeValue("desc");
        if (desc != null) {
            menuElement.setDesc(desc);
        }
        // 加入而不是重置
        menuElement.getDescs().putAll(getDescs(domElement));

        menuElement.setProfiles(getProfiles(domElement));
    }

    private List<MenuItem> getItems(Element element, Map<String, Object> parentOptions) throws Exception {
        List<MenuItem> items = new ArrayList<>();
        for (Element itemElement : element.elements("item")) {
            items.add(getItem(itemElement, parentOptions));
        }
        return items;
    }

    private MenuItem getItem(Element element, Map<String, Object> parentOptions) throws Exception {
        MenuItem item = new MenuItem();
        parseElementCommon(item, element, parentOptions);
        item.setPath(element.attributeValue("path"));
        item.setRank(element.attributeValue("rank"));
        item.setPermission(getPermission(element));
        item.setIcon(element.attributeValue("icon"));
        item.setTarget(element.attributeValue("type"));
        item.setTarget(element.attributeValue("target"));
        item.setSubs(getItems(element, item.getOptions()));
        return item;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String getPermission(Element element) throws ClassNotFoundException {
        String permission = element.attributeValue("permission");
        String className = element.attributeValue("permission-class");
        if (StringUtils.isNotBlank(className)) {
            Class<?> clazz = this.classLoader.loadClass(className);
            if (clazz.isEnum()) { // 如果为枚举类型，则取枚举的名称
                Enum<?> enumConstant = Enum.valueOf((Class<Enum>) clazz, permission);
                permission = enumConstant.name();
            } else { // 否则取该类型的静态字符串属性值
                Object value = ClassUtil.getPublicStaticPropertyValue(clazz, permission);
                permission = (String) value; // 属性值必须为字符串
            }
        }
        return permission;
    }

    private Map<Locale, String> getCaptions(Element element) {
        Map<Locale, String> captions = new HashMap<>();
        for (Element captionElement : element.elements("caption")) {
            Locale locale = new Locale(captionElement.attributeValue("locale"));
            captions.put(locale, captionElement.getTextTrim());
        }
        return captions;
    }

    private Map<Locale, String> getDescs(Element element) {
        Map<Locale, String> descs = new HashMap<>();
        for (Element descElement : element.elements("desc")) {
            Locale locale = new Locale(descElement.attributeValue("locale"));
            descs.put(locale, descElement.getTextTrim());
        }
        return descs;
    }

    private Set<String> getProfiles(Element element) {
        String profile = element.attributeValue("profile");
        Set<String> profiles = StringUtil.splitToSet(profile, Strings.COMMA, true);
        return profiles == null ? Collections.emptySet() : profiles;
    }

    private Map<String, Object> getOptions(Element element, Map<String, Object> parentOptions)
            throws Exception {
        Map<String, Object> options = new HashMap<>();
        Element optionsElement = element.element("options");
        // 需要继承父级选项集时，先继承父级选项集
        if (requiresInheritOptions(optionsElement, parentOptions)) {
            options.putAll(parentOptions);
        }
        // 设置了选项集则再加入自身选项集
        if (optionsElement != null) {
            options.putAll(getOption(optionsElement));
        }
        return options;
    }

    private boolean requiresInheritOptions(Element optionsElement,
            Map<String, Object> parentOptions) {
        if (parentOptions == null || parentOptions.isEmpty()) { // 如果父级选项集为空，则不需要继承
            return false;
        }
        if (optionsElement == null) { // 未设置选项集则默认需要继承
            return true;
        }
        // 设置有选项集，则根据inherit属性判断，inherit未设置时默认视为false
        return Boolean.parseBoolean(optionsElement.attributeValue("inherit"));
    }

    private Map<String, Object> getOption(Element element) throws Exception {
        Map<String, Object> options = new HashMap<>();
        for (Element optionElement : element.elements("option")) {
            String className = optionElement.attributeValue("type");
            String value = optionElement.getTextTrim();
            if (StringUtils.isNotBlank(className)) {
                Class<?> type = this.classLoader.loadClass(className);
                Object typedValue = AlgoParseString.visit(value, type);
                options.put(optionElement.attributeValue("name"), typedValue);
            } else {
                options.put(optionElement.attributeValue("name"), value);
            }
        }
        return options;
    }
}
