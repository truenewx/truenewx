package org.truenewx.tnxjee.webmvc.api.meta;

import java.util.Locale;
import java.util.Map;

import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyMeta;

/**
 * API模型元数据解决器
 *
 * @author jianglei
 */
public interface ApiModelMetaResolver {

    Map<String, ApiModelPropertyMeta> resolve(Class<? extends Model> modelClass, Locale locale);

}
