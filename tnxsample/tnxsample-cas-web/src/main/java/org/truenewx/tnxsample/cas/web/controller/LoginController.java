package org.truenewx.tnxsample.cas.web.controller;

import org.springframework.stereotype.Controller;
import org.truenewx.tnxjeex.cas.server.controller.CasServerLoginControllerSupport;

@Controller
public class LoginController extends CasServerLoginControllerSupport {

    @Override
    protected String getDefaultAppName() {
        return "admin";
    }

}
