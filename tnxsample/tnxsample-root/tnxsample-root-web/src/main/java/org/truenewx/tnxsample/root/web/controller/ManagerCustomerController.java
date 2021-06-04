package org.truenewx.tnxsample.root.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAuthority;
import org.truenewx.tnxsample.common.constant.UserTypes;
import org.truenewx.tnxsample.root.api.ManagerCustomerApi;
import org.truenewx.tnxsample.root.model.entity.Customer;
import org.truenewx.tnxsample.root.service.CustomerService;

@Caption("客户管理")
@RestController
public class ManagerCustomerController implements ManagerCustomerApi {

    @Autowired
    private CustomerService customerService;

    @Override
    @Caption("根据手机号码加载客户")
    @ConfigAuthority(type = UserTypes.MANAGER)
    @ResultFilter(type = Customer.class, excluded = { "accountNonExpired", "accountNonLocked",
            "credentialsNonExpired", "enabled", "password" }, pureEnum = "type")
    public Customer loadByCellphone(String cellphone) {
        return this.customerService.loadByCellphone(cellphone);
    }

}
