package org.truenewx.tnxsample.root.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.core.caption.Caption;
import org.truenewx.tnxjee.test.service.annotation.TestBusinessException;
import org.truenewx.tnxsample.root.model.entity.Customer;
import org.truenewx.tnxsample.root.service.test.ServiceTestSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * CustomerServiceTest
 *
 * @author jianglei
 */
public class CustomerServiceTest extends ServiceTestSupport {
    @Autowired
    private CustomerService service;

    @Test
    @Caption("测试：根据手机号码加载客户")
    public void loadByCellphoneTest() {
        Customer customer0 = getData(Customer.class, 0);
        String cellphone = customer0.getCellphone();
        Customer customer = this.service.loadByCellphone(cellphone);
        assertEquals(cellphone, customer.getCellphone());
        assertEquals(customer0.getId(), customer.getId());
    }

    @Test
    @Caption("测试：根据不存在的手机号码加载客户")
    @TestBusinessException(CustomerExceptionCodes.NO_CELLPHONE)
    public void loadByNoCellphoneTest() {
        this.service.loadByCellphone("12312345678");
        fail();
    }

    @Test
    @Caption("测试：修改客户禁用状态")
    public void updateDisabledTest() {
        Customer customer0 = getData(Customer.class, 0);
        boolean disabled = !customer0.isDisabled();
        Customer customer = this.service.updateDisabled(customer0.getId(), disabled);
        assertEquals(disabled, customer.isDisabled());
    }
}
