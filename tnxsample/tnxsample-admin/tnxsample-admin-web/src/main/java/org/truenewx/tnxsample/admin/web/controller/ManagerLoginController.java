package org.truenewx.tnxsample.admin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;
import org.truenewx.tnxsample.admin.api.ManagerLoginApi;
import org.truenewx.tnxsample.admin.model.entity.Manager;
import org.truenewx.tnxsample.admin.service.ManagerService;

/**
 * 管理员登录控制器
 *
 * @author jianglei
 */
@RestController
public class ManagerLoginController implements ManagerLoginApi {

    @Autowired
    private ManagerService managerService;

    @Override
    @ConfigAnonymous
    public UserSpecificDetails<? extends IntegerUserIdentity> validate(String username, String password) {
        Manager manager = this.managerService.validateLogin(username, password);
        return manager.getSpecificDetails();
    }

}
