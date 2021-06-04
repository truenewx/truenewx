package org.truenewx.tnxsample.root.service;

import org.truenewx.tnxjee.service.unity.UnityService;
import org.truenewx.tnxsample.root.model.entity.Customer;

/**
 * 用户服务
 *
 * @author jianglei
 */
public interface CustomerService extends UnityService<Customer, Integer> {

    Customer loadByCellphone(String cellphone);

    Customer updateDisabled(int userId, boolean disabled);

}
