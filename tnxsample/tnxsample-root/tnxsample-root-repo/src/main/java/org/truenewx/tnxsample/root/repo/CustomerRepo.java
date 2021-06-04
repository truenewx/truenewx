package org.truenewx.tnxsample.root.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.truenewx.tnxsample.root.model.entity.Customer;

/**
 * 客户Repo
 *
 * @author jianglei
 */
public interface CustomerRepo extends JpaRepository<Customer, Integer> {

    Customer findByCellphone(String cellphone);

}
