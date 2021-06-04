package org.truenewx.tnxsample.root.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.impl.unity.AbstractUnityService;
import org.truenewx.tnxsample.root.model.entity.Customer;
import org.truenewx.tnxsample.root.repo.CustomerRepo;

/**
 * 客户服务实现
 *
 * @author jianglei
 */
@Service
public class CustomerServiceImpl extends AbstractUnityService<Customer, Integer>
        implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public Customer loadByCellphone(String cellphone) {
        Customer customer = this.customerRepo.findByCellphone(cellphone);
        if (customer == null) {
            throw new BusinessException(CustomerExceptionCodes.NO_CELLPHONE, cellphone);
        }
        return customer;
    }

    @Override
    public Customer updateDisabled(int userId, boolean disabled) {
        Customer customer = find(userId);
        if (customer != null) {
            customer.setDisabled(disabled);
            this.customerRepo.save(customer);
        }
        return customer;
    }

}
