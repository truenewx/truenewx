package org.truenewx.tnxjee.webmvc.feign;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.webmvc.exception.parser.ResolvableExceptionParser;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Feign错误解码器
 *
 * @author jianglei
 */
@Component
public class FeignErrorDecoder extends ErrorDecoder.Default {

    @Autowired
    private ResolvableExceptionParser exceptionParser;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            int status = response.status();
            if (status == HttpStatus.FORBIDDEN.value() || status == HttpStatus.BAD_REQUEST.value()) {
                String json = IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
                return this.exceptionParser.parse(json);
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
        return super.decode(methodKey, response);
    }

}
