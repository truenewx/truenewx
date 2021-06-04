package org.truenewx.tnxjee.webmvc.feign;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.FormatException;
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.SingleException;
import org.truenewx.tnxjee.service.exception.model.ExceptionError;
import org.truenewx.tnxjee.webmvc.exception.model.ExceptionErrorBody;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * 异常错误解码器
 *
 * @author jianglei
 */
@Component
public class ExceptionErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.status() == HttpStatus.FORBIDDEN.value()) {
                String json = IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
                ExceptionErrorBody body = JsonUtil.json2Bean(json, ExceptionErrorBody.class);
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
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
        return super.decode(methodKey, response);
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
