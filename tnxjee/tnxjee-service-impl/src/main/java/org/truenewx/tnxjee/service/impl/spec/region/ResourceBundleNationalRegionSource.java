package org.truenewx.tnxjee.service.impl.spec.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.message.MessagesSource;
import org.truenewx.tnxjee.service.spec.region.Region;

/**
 * 基于资源绑定属性文件的国家级区划来源实现
 *
 * @author jianglei
 */
@Component
public class ResourceBundleNationalRegionSource extends AbstractNationalRegionSource {

    /**
     * 消息集来源
     */
    @Autowired(required = false)
    private MessagesSource messagesSource;
    /**
     * 区域选项映射集解析器
     */
    @Autowired
    private RegionMapParser parser;

    /**
     * 构建指定显示区域的当前国家行政区划
     *
     * @param locale 显示区域
     * @return 当前国家行政区划
     */
    @Override
    protected Region buildNationalRegion(Locale locale) {
        String nation = getNationCode();
        Map<String, String> messages = this.messagesSource.getMessages(locale, nation, true);
        String nationCaption = messages.get(nation);
        if (nationCaption != null) { // 取得到国家显示名才构建国家级区域选项
            Region nationalRegion = new Region(nation, nationCaption);
            if (this.parser != null) {
                Iterable<Region> subs = this.parser.parseAll(nation, messages);

                Map<String, Region> codeSubsMap = new HashMap<>();
                Map<String, Region> shortCaptionSubsMap = new HashMap<>();
                Map<String, Region> fullCaptionSubsMap = new HashMap<>();
                for (Region sub : subs) {
                    codeSubsMap.put(sub.getCode(), sub);
                    StringBuilder shortCaption = new StringBuilder(sub.getCaption());
                    StringBuilder fullCaption = new StringBuilder(sub.getFullCaption());
                    Region parent = sub.getParent();
                    if (parent == null) { // 所有子选项中未指定父选项的才作为下一级子选项加入国家级选项中
                        nationalRegion.addSub(sub);
                    }
                    while (parent != null && !parent.getCode().equals(nation)) { // 不加国别名称
                        shortCaption.insert(0, parent.getCaption());
                        fullCaption.insert(0, parent.getFullCaption());
                        parent = parent.getParent();
                    }
                    shortCaptionSubsMap.put(shortCaption.toString(), sub);
                    fullCaptionSubsMap.put(fullCaption.toString(), sub);
                }
                this.localeCodeSubsMap.put(locale, codeSubsMap);
                this.localeShortCaptionSubsMap.put(locale, shortCaptionSubsMap);
                this.localeFullCaptionSubsMap.put(locale, fullCaptionSubsMap);
            }
            this.localeNationalRegionMap.put(locale, nationalRegion);
            return nationalRegion;
        }
        return null;
    }

}
