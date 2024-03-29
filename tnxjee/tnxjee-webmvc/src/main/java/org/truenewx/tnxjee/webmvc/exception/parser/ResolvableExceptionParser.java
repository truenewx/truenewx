package org.truenewx.tnxjee.webmvc.exception.parser;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.service.exception.*;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;
import org.truenewx.tnxjee.webmvc.exception.model.ExceptionErrorBody;

/**
 * 可解决异常解析器
 */
@Component
public class ResolvableExceptionParser {

    public ResolvableException parse(String json) {
        try {
            ExceptionErrorBody body = JsonUtil.json2Bean(json, ExceptionErrorBody.class);
            if (body != null) {
                ExceptionError[] errors = body.getErrors();
                if (errors != null) {
                    if (errors.length == 1) {
                        return buildException(errors[0]);
                    } else if (errors.length > 1) {
                        SingleException[] exceptions = new SingleException[errors.length];
                        for (int i = 0; i < errors.length; i++) {
                            exceptions[i] = buildException(errors[i]);
                        }
                        return new MultiException(exceptions);
                    }
                }
            }
        } catch (Exception ignored) {
            // 忽略解析过程中产生的运行期异常，以免干扰正常的错误处理
        }
        return null;
    }

    private SingleException buildException(ExceptionError error) throws ClassNotFoundException {
        String type = error.getType();
        Class<?> clazz = Class.forName(type);
        if (BusinessException.class.isAssignableFrom(clazz)) {
            return new BusinessException(error);
        } else if (clazz == FormatException.class) {
            return new FormatException(error);
        }
        return null;
    }

}
