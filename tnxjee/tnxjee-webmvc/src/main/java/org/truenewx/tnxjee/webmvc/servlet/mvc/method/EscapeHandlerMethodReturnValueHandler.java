package org.truenewx.tnxjee.webmvc.servlet.mvc.method;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjee.web.util.WebUtil;

/**
 * 转义处理方法结果值处理器
 */
public class EscapeHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler delegate;

    public EscapeHandlerMethodReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        Assert.notNull(delegate, "The delegate must be not null");
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return this.delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (MethodParameterEscapeProcessor.INSTANCE.supports(returnType)) {
            if (webRequest instanceof ServletWebRequest) {
                HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
                Terminal terminal = WebUtil.getRequestTerminal(request);
                if (terminal.getProgram() == Program.NATIVE) { // 原生应用才需要反转义
                    returnValue = MethodParameterEscapeProcessor.INSTANCE.escape(returnValue, true);
                }
            }
        }

        this.delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

}
