package org.truenewx.tnxjee.webmvc.api.meta;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.spec.EnumGrouped;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiApp;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiContext;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * API元数据控制器
 */
@RestController
@RequestMapping("/api/meta")
public class ApiMetaController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private ApiModelMetaResolver metaResolver;
    @Autowired
    private EnumDictResolver enumDictResolver;
    @Autowired
    private ApiMetaProperties apiMetaProperties;
    @Autowired
    private CommonProperties commonProperties;
    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String baseApp;

    @GetMapping("/context")
    public ApiContext context() {
        ApiContext context = new ApiContext();
        context.setBaseApp(this.baseApp);
        context.setLoginSuccessRedirectParameter(this.apiMetaProperties.getRedirectTargetUrlParameter());
        Set<String> appNames = ArrayUtil.toSet(this.apiMetaProperties.getAppNames());
        if (CollectionUtils.isEmpty(appNames)) {
            appNames = new HashSet<>(this.commonProperties.getApps().keySet());
        } else {
            appNames.add(this.baseApp); // 至少包含当前应用
        }
        for (String appName : appNames) {
            AppConfiguration appConfiguration = this.commonProperties.getApp(appName);
            if (appConfiguration != null) {
                ApiApp apiApp = new ApiApp(appConfiguration.getContextUri(false), appConfiguration.getSubs());
                context.getApps().put(appName, apiApp);
            }
        }
        return context;
    }

    @GetMapping("/method")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption" })
    @ResultFilter(type = ApiModelPropertyMeta.class, pureEnum = "type")
    public Map<String, ApiModelPropertyMeta> method(@RequestParam("url") String url, HttpServletRequest request) {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(url, HttpMethod.POST);
        if (handlerMethod != null) {
            for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
                if (methodParameter.getParameterAnnotation(RequestBody.class) != null) {
                    return getMetas(methodParameter.getParameterType(), request.getLocale());
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, ApiModelPropertyMeta> getMetas(Class<?> clazz, Locale locale) {
        if (Model.class.isAssignableFrom(clazz)) {
            Class<? extends Model> modelClass = (Class<? extends Model>) clazz;
            Map<String, ApiModelPropertyMeta> metas = this.metaResolver.resolve(modelClass, locale);
            return metas; // 为了便于调试，抽取变量
        }
        return null;
    }

    @GetMapping("/model")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption" })
    @ResultFilter(type = ApiModelPropertyMeta.class, pureEnum = "type")
    public Map<String, ApiModelPropertyMeta> model(@RequestParam("type") String type, HttpServletRequest request) {
        try {
            ClassLoader classLoader = this.context.getClassLoader();
            if (classLoader != null) {
                Class<?> clazz = classLoader.loadClass(type);
                return getMetas(clazz, request.getLocale());
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/enums")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption", "searchIndex", "children", "attachment" })
    public Collection<EnumItem> enumItems(@RequestParam("type") String type,
            @RequestParam(value = "subtype", required = false) String subtype,
            @RequestParam(value = "grouped", defaultValue = "false") boolean grouped, HttpServletRequest request) {
        Locale locale = request.getLocale();
        EnumType enumType = this.enumDictResolver.getEnumType(type, subtype, locale);
        if (enumType != null) {
            Collection<EnumItem> items = enumType.getItems();
            if (grouped) { // 考虑分组
                try {
                    Class<?> clazz = Objects.requireNonNull(this.context.getClassLoader()).loadClass(type);
                    if (clazz.isEnum() && EnumGrouped.class.isAssignableFrom(clazz)) {
                        Map<Enum<?>, EnumItem> groupEnumItemMap = new TreeMap<>();
                        for (EnumItem item : items) {
                            EnumGrouped<?> enumConstant = (EnumGrouped<?>) Enum.valueOf((Class<Enum>) clazz,
                                    item.getKey());
                            Enum<?> groupEnum = enumConstant.group();
                            EnumItem groupEnumItem = groupEnumItemMap.get(groupEnum);
                            if (groupEnumItem == null) {
                                groupEnumItem = this.enumDictResolver.getEnumItem(groupEnum, locale);
                                groupEnumItemMap.put(groupEnum, groupEnumItem);
                            }
                            groupEnumItem.addChild(item);
                        }
                        return groupEnumItemMap.values();
                    }
                } catch (ClassNotFoundException ignored) {
                    // 忽略
                }
            }
            return items;
        }
        return Collections.emptyList();
    }

}
