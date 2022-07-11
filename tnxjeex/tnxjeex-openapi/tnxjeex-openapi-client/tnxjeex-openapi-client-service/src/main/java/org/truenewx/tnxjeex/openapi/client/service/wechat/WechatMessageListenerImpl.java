package org.truenewx.tnxjeex.openapi.client.service.wechat;

import java.util.*;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessage;
import org.truenewx.tnxjeex.openapi.client.model.wechat.WechatMessageType;
import org.truenewx.tnxjeex.openapi.client.service.NoSuchMessageHandlerException;

/**
 * 微信开放接口消息侦听器实现
 *
 * @author jianglei
 */
@Service
public class WechatMessageListenerImpl implements WechatMessageListener, ContextInitializedBean {

    @Autowired
    private ExecutorService executorService;
    private Map<WechatMessageType, List<WechatMessageHandler>> handlerMapping = new HashMap<>();

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, WechatMessageHandler> beans = context.getBeansOfType(WechatMessageHandler.class);
        beans.values().forEach(handler -> {
            WechatMessageType messageType = handler.getMessageType();
            List<WechatMessageHandler> handlers = this.handlerMapping.computeIfAbsent(messageType,
                    k -> new ArrayList<>());
            handlers.add(handler);
            Collections.sort(handlers);
        });
    }

    @Override
    public WechatMessage onReceived(WechatMessage message) throws NoSuchMessageHandlerException {
        if (message != null) {
            List<WechatMessageHandler> handlers = this.handlerMapping.get(message.getType());
            if (CollectionUtils.isEmpty(handlers)) {
                throw new NoSuchMessageHandlerException();
            }
            for (WechatMessageHandler handler : handlers) {
                if (handler instanceof WechatMessageSyncHandler) { // 同步处理
                    WechatMessageSyncHandler syncHandler = (WechatMessageSyncHandler) handler;
                    WechatMessage result = syncHandler.handleMessage(message);
                    if (result != null) {
                        return result;
                    }
                } else if (handler instanceof WechatMessageAsynHandler) { // 异步处理
                    WechatMessageAsynHandler asynHandler = (WechatMessageAsynHandler) handler;
                    this.executorService.submit(() -> asynHandler.handleMessage(message));
                }
            }
        }
        return null;
    }

}
