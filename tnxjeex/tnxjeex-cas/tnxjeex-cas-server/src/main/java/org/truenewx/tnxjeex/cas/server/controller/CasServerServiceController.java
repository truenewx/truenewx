package org.truenewx.tnxjeex.cas.server.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxjeex.cas.server.service.CasServiceManager;
import org.truenewx.tnxjeex.cas.server.ticket.CasTicketManager;

/**
 * Cas服务端服务控制器
 *
 * @author jianglei
 */
@Controller
public class CasServerServiceController {

    @Autowired
    private CasServiceManager serviceManager;
    @Autowired
    private CasTicketManager ticketManager;

    @GetMapping("/serviceValidate")
    @ConfigAnonymous
    @ResponseBody
    public Assertion serviceValidate(@RequestParam("service") String service,
            @RequestParam("ticket") String ticket) {
        service = URLDecoder.decode(service, StandardCharsets.UTF_8);
        String app = this.serviceManager.getAppName(service);
        return this.ticketManager.validateAppTicket(app, ticket);
    }

}
