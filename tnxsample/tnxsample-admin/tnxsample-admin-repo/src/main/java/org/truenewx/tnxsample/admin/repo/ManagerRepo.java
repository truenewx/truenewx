package org.truenewx.tnxsample.admin.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.truenewx.tnxsample.admin.model.entity.Manager;

/**
 * 管理员Repository
 *
 * @author jianglei
 */
public interface ManagerRepo extends JpaRepository<Manager, Integer>, ManagerRepox {

    Manager findFirstByUsername(String username);

}
