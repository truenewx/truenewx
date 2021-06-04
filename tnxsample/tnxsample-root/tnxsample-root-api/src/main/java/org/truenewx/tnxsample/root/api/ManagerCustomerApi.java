package org.truenewx.tnxsample.root.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.truenewx.tnxjee.core.api.RpcApi;
import org.truenewx.tnxsample.root.model.entity.Customer;

/**
 * 客户管理API
 *
 * @author jianglei
 */
@RpcApi
@RequestMapping("/manager/customer")
public interface ManagerCustomerApi {

    @GetMapping("/payload/cellphone/{cellphone}")
    Customer loadByCellphone(@PathVariable String cellphone);

}
